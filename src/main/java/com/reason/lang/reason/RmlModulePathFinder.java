package com.reason.lang.reason;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseModulePathFinder;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RmlModulePathFinder extends BaseModulePathFinder {

    // Find the expression paths
    @NotNull
    public List<String> extractPotentialPaths(@NotNull PsiElement element) {
        List<String> qualifiedNames = new ArrayList<>();

        String path = extractPathName(element, RmlTypes.INSTANCE);
        if (!path.isEmpty()) {
            PsiQualifiedNamedElement moduleAlias = PsiFinder.getInstance().findModuleAlias(element.getProject(), path);
            String modulePath = moduleAlias == null ? path : moduleAlias.getQualifiedName();
            qualifiedNames.add(modulePath);
            qualifiedNames.add(((FileBase) element.getContainingFile()).asModuleName() + "." + modulePath);
        }

        // Walk backward until top of the file is reached, trying to find local opens and opens/includes
        PsiElement item = element;
        while (item != null) {
            if (100 < qualifiedNames.size()) {
                break; // There must be a problem with the parser
            }

            if (item instanceof FileBase) {
                qualifiedNames.add(((FileBase) item).asModuleName());
                break;
            } else if (item instanceof PsiOpen || item instanceof PsiInclude) {
                String openName = ((PsiNamedElement) item).getName();
                // Add open value to all previous elements
                List<String> withOpenQualifier = qualifiedNames.stream().map(name -> openName + "." + name).collect(Collectors.toList());
                qualifiedNames.addAll(withOpenQualifier);

                qualifiedNames.add(openName);
            } else if (item instanceof PsiModule) {
                PsiModule module = (PsiModule) item;
                String moduleName = module.getName();
                String moduleAlias = findModuleAlias(element.getProject(), module.getAlias());

                if (moduleAlias != null && !moduleAlias.equals(moduleName)) {
                    // Replace module name in resolved paths with the module alias
                    qualifiedNames = qualifiedNames.stream().map(name -> {
                        if (name.equals(moduleName)) {
                            return moduleAlias;
                        } else if (name.startsWith(moduleName + ".")) {
                            int length = moduleAlias.length();
                            if (length <= moduleName.length()) {
                                return moduleAlias + "." + moduleName.substring(length);
                            }
                        } else if (name.endsWith("." + moduleName)) {
                            return name.replace("." + moduleName, "." + moduleAlias);
                        }
                        return name;
                    }).collect(Collectors.toList());
                }
            }

            PsiElement prevItem = item.getPrevSibling();
            if (prevItem == null) {
                PsiElement parent = item.getParent();
                if (parent instanceof PsiLocalOpen) {
                    String localOpenName = ((PsiLocalOpen) parent).getName();
                    String localOpenAlias = findModuleAlias(element.getProject(), localOpenName);
                    qualifiedNames.add(localOpenAlias == null || localOpenAlias.equals(localOpenName) ? localOpenName : localOpenAlias);
                }
                item = parent;
            } else {
                item = prevItem;
            }
        }

        qualifiedNames.add("Pervasives");
        return qualifiedNames;
    }

}
