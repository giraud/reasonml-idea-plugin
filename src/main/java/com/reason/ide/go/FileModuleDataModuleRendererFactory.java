package com.reason.ide.go;

import com.intellij.ide.util.*;
import com.intellij.util.*;
import org.jetbrains.annotations.*;

public class FileModuleDataModuleRendererFactory extends ModuleRendererFactory {
    @Override protected boolean handles(Object element) {
        return element instanceof ORModuleContributor.FileModuleDataNavigationItem;
    }

    @Override public @Nullable TextWithIcon getModuleTextWithIcon(Object element) {
        ORModuleContributor.FileModuleDataNavigationItem dataNavigation = (ORModuleContributor.FileModuleDataNavigationItem) element;
        return new TextWithIcon(dataNavigation.getLocation(),  dataNavigation.getLocationIcon());
    }
}
