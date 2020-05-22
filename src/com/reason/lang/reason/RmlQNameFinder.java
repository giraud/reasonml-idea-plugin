package com.reason.lang.reason;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ArrayListSet;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseQNameFinder;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.reference.ORFakeResolvedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RmlQNameFinder extends BaseQNameFinder {

    // Find the expression paths
    @NotNull
    public Set<String> extractPotentialPaths(@NotNull PsiElement element, boolean resolveLocalModuleAlias) {
        Set<String> qualifiedNames = new ArrayListSet<>();

        PsiElement sourceElement = element instanceof ORFakeResolvedElement ? element.getOriginalElement() : element;
        String filePath = ((FileBase) sourceElement.getContainingFile()).getModuleName() + ".";
        String path = extractPathName(sourceElement, RmlTypes.INSTANCE);
        String pathExtension = path.isEmpty() ? "" : "." + path;

        // Walk backward until top of the file is reached, trying to find local opens and opens/includes
        PsiElement item = sourceElement;
        while (item != null) {
            if (100 < qualifiedNames.size()) {
                break; // There must be a problem with the parser
            }

            if (item instanceof FileBase) {
                qualifiedNames.add(((FileBase) item).getModuleName() + pathExtension);
                break;
            } else if (item instanceof PsiInnerModule) {
                PsiInnerModule module = (PsiInnerModule) item;
                String moduleQName = module.getQualifiedName();

                if (resolveLocalModuleAlias) {
                    String alias = module.getAlias();
                    if (alias != null) {
                        // This is a local module alias, we'll need to replace it in final paths
                        Pattern compile = Pattern.compile("(\\.?)(" + module.getModuleName() + ")(\\.?)");
                        String replace = "$1" + alias + "$3";
                        qualifiedNames = qualifiedNames.stream().map(name -> {
                            Matcher matcher = compile.matcher(name);
                            if (matcher.find()) {
                                return matcher.replaceAll(replace);
                            }
                            return name;
                        }).collect(Collectors.toCollection(ArrayListSet::new));
                        path = compile.matcher(path).replaceAll(replace);
                        pathExtension = compile.matcher(pathExtension).replaceAll(replace);
                    }
                }

                if (path.equals(moduleQName)) {
                    qualifiedNames.add(((FileBase) sourceElement.getContainingFile()).getModuleName() + pathExtension);
                }
            } else if (item instanceof PsiLocalOpen) {
                String openName = extractPathName(item, RmlTypes.INSTANCE);
                // Add local open value to all previous elements
                qualifiedNames.addAll(extendPathWith(filePath, openName, qualifiedNames));
                qualifiedNames.add(openName + pathExtension);
                qualifiedNames.add(((FileBase) sourceElement.getContainingFile()).getModuleName() + "." + openName + pathExtension);
            } else if (item instanceof PsiOpen || item instanceof PsiInclude) {
                String openName = ((PsiQualifiedElement) item).getQualifiedName();
                // Add open value to all previous elements
                qualifiedNames.addAll(extendPathWith(filePath, openName, qualifiedNames));
                qualifiedNames.add(openName + pathExtension);
            } else if (item instanceof PsiLetBinding) {
                // let a = { <caret> }
                PsiLet let = PsiTreeUtil.getParentOfType(item, PsiLet.class);
                if (let != null) {
                    String letQName = let.getQualifiedName();
                    qualifiedNames.addAll(extendPathWith(filePath, letQName, qualifiedNames));
                    qualifiedNames.add(letQName + pathExtension);
                }
            } else if (item instanceof PsiFunction) {
                PsiQualifiedElement parent = PsiTreeUtil.getParentOfType(item, PsiQualifiedElement.class);
                if (parent != null) {
                    String parentQName = parent.getQualifiedName();
                    // Register all parameters of function
                    for (PsiParameter parameter : ((PsiFunction) item).getParameters()) {
                        qualifiedNames.add(parentQName + "[" + parameter.getName() + "]");
                    }
                }
            }

            PsiElement prevItem = item.getPrevSibling();
            if (prevItem == null) {
                item = item.getParent();
            } else {
                item = prevItem;
            }
        }

        if (!path.isEmpty()) {
            qualifiedNames.add(path);
        }

        return qualifiedNames;
    }

}
