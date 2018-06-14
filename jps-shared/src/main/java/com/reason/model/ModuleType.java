package com.reason.model;

import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.ex.JpsElementTypeWithDummyProperties;
import org.jetbrains.jps.model.module.JpsModuleType;

public class ModuleType extends JpsElementTypeWithDummyProperties implements JpsModuleType<JpsDummyElement>{
    public static final ModuleType INSTANCE = new ModuleType();

    private ModuleType(){
    }
}
