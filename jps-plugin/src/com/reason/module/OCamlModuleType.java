package com.reason.module;

import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.module.ModuleType;
import icons.ORIcons;

/**
 * Type of OCaml native project.
 */
public class OCamlModuleType extends ModuleType<OCamlModuleBuilder> {

    private static final OCamlModuleType INSTANCE = new OCamlModuleType();

    private OCamlModuleType() {
        super("ocamlModuleType");
    }

    @NotNull
    public static OCamlModuleType getInstance() {
        return INSTANCE;
    }

    @NotNull
    @Override
    public OCamlModuleBuilder createModuleBuilder() {
        return new OCamlModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        //noinspection DialogTitleCapitalization
        return "OCaml module";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "OCaml modules are used for native development";
    }

    @NotNull
    @Override
    public Icon getNodeIcon(boolean isOpened) {
        return ORIcons.OCL_MODULE;
    }
}
