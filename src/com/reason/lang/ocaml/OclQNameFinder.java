package com.reason.lang.ocaml;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ArrayListSet;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseQNameFinder;
import com.reason.lang.QNameFinder;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.PsiLocalOpen;
import com.reason.lang.core.psi.reference.ORFakeResolvedElement;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import org.jetbrains.annotations.*;

import static java.util.Collections.emptyList;

public class OclQNameFinder extends BaseQNameFinder {

  public static final QNameFinder INSTANCE = new OclQNameFinder();

  private OclQNameFinder() {}

  // Find the expression paths
  @NotNull
  public Set<String> extractPotentialPaths(@Nullable PsiElement element) {
    Set<String> qualifiedNames = new ArrayListSet<>();
    if (element == null) {
      return qualifiedNames;
    }

    PsiElement sourceElement = element instanceof ORFakeResolvedElement ? element.getOriginalElement() : element;
    String filePath = ((FileBase) sourceElement.getContainingFile()).getModuleName() + ".";
    String path = extractPathName(sourceElement, OclTypes.INSTANCE);
    String pathExtension = path.isEmpty() ? "" : "." + path;

    // See {@link com.reason.lang.reason.RmlQNameFinder}
    Set<String> resolvedQualifiedNames = new ArrayListSet<>();
    String resolvedPath = path;
    String resolvedPathExtension = pathExtension;

    // Walk backward until top of the file is reached, trying to find local opens and opens/includes
    PsiElement item = sourceElement;
    while (item != null) {
      if (100 < qualifiedNames.size()) {
        break; // There must be a problem with the parser
      }

      if ((item instanceof FileBase)) {
        String moduleName = ((FileBase) item).getModuleName();
        qualifiedNames.add(moduleName + pathExtension);
        resolvedQualifiedNames.add(moduleName + resolvedPathExtension);
        break;
      } else if (item instanceof PsiInnerModule) {
        PsiInnerModule module = (PsiInnerModule) item;
        String moduleQName = module.getQualifiedName();

        String alias = module.getAlias();
        if (alias != null) {
          // This is a local module alias, we'll need to replace it in final paths
          Pattern compile = Pattern.compile("(\\.?)(" + module.getModuleName() + ")(\\.?)");
          String replace = "$1" + alias + "$3";
          resolvedQualifiedNames =
              resolvedQualifiedNames
                  .stream()
                  .map(
                      name -> {
                        Matcher matcher = compile.matcher(name);
                        if (matcher.find()) {
                          return matcher.replaceAll(replace);
                        }
                        return name;
                      })
                  .collect(Collectors.toCollection(ArrayListSet::new));
          resolvedPath = compile.matcher(resolvedPath).replaceAll(replace);
          resolvedPathExtension = compile.matcher(resolvedPathExtension).replaceAll(replace);
        }

        if (path.equals(moduleQName)) {
          String moduleName = ((FileBase) sourceElement.getContainingFile()).getModuleName();
          qualifiedNames.add(moduleName + pathExtension);
          resolvedQualifiedNames.add(moduleName + resolvedPathExtension);
        }
      } else if (item instanceof PsiLocalOpen) {
        String openName = extractPathName(item, OclTypes.INSTANCE);
        // Add local open value to all previous elements
        qualifiedNames.addAll(extendPathWith(filePath, openName, qualifiedNames));
        qualifiedNames.add(openName + pathExtension);
        // Same for resolved elements
        resolvedQualifiedNames.addAll(extendPathWith(filePath, openName, resolvedQualifiedNames));
        resolvedQualifiedNames.add(openName + resolvedPathExtension);
      } else if (item instanceof PsiOpen || item instanceof PsiInclude) {
        String openName = ((PsiQualifiedElement) item).getQualifiedName();
        String moduleName = ((FileBase) sourceElement.getContainingFile()).getModuleName();
        // Add open value to all previous elements
        qualifiedNames.addAll(extendPathWith(filePath, openName, qualifiedNames));
        qualifiedNames.add(openName + pathExtension);
        qualifiedNames.add(moduleName + "." + openName + pathExtension);
        // Same for resolved elements
        resolvedQualifiedNames.addAll(extendPathWith(filePath, openName, resolvedQualifiedNames));
        resolvedQualifiedNames.add(openName + resolvedPathExtension);
        resolvedQualifiedNames.add(moduleName + "." + openName + resolvedPathExtension);
      } else if (item instanceof PsiLetBinding) {
        // let a = { <caret> }
        PsiLet let = PsiTreeUtil.getParentOfType(item, PsiLet.class);
        String name = let == null ? null : let.getName();
        if (name != null) {
          String letQName = let.getQualifiedName();
          qualifiedNames.addAll(extendPathWith(filePath, letQName, qualifiedNames));
          qualifiedNames.add(letQName + pathExtension);
          // Same for resolved elements
          resolvedQualifiedNames.addAll(extendPathWith(filePath, letQName, resolvedQualifiedNames));
          resolvedQualifiedNames.add(letQName + resolvedPathExtension);
          // If function, register all parameters of function
          if (let.isFunction()) {
            PsiFunction function = let.getFunction();
            List<PsiParameter> parameters = function == null ? emptyList() : function.getParameters();
            for (PsiParameter parameter : parameters) {
              String paramQName = letQName + "[" + parameter.getName() + "]";
              qualifiedNames.add(paramQName);
              // Same for resolved elements
              resolvedQualifiedNames.add(paramQName);
            }
          }
        }
      }
      /*
      else if (item instanceof PsiFunction) {
        PsiQualifiedElement parent = PsiTreeUtil.getParentOfType(item, PsiQualifiedElement.class);
        if (parent != null) {
          String parentQName = parent.getQualifiedName();
          // Register all parameters of function
          for (PsiParameter parameter : ((PsiFunction) item).getParameters()) {
            String paramQName = parentQName + "[" + parameter.getName() + "]";
            qualifiedNames.add(paramQName);
            // Same for resolved elements
            resolvedQualifiedNames.add(paramQName);
          }
        }
      }
      */

      PsiElement prevItem = item.getPrevSibling();
      if (prevItem == null) {
        item = item.getParent();
      } else {
        item = prevItem;
      }
    }

    if (!path.isEmpty()) {
      qualifiedNames.add(path);
      resolvedQualifiedNames.add(resolvedPath);
    }

    qualifiedNames.addAll(resolvedQualifiedNames);
    qualifiedNames.add("");
    qualifiedNames.add("Pervasives");
    return qualifiedNames;
  }
}
