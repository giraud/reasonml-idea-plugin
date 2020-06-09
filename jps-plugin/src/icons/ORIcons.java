package icons;

import javax.swing.*;

import static com.intellij.openapi.util.IconLoader.getIcon;

/*
 * https://www.jetbrains.org/intellij/sdk/docs/reference_guide/work_with_icons_and_images.html
 * https://jetbrains.design/intellij/principles/icons/
 *
 * Node, action, filetype : 16x16
 * Tool window            : 13x13
 * Editor gutter          : 12x12
 * Font                   : Gotham
 */
public class ORIcons {
    // TOOL WINDOW

    public static final Icon BUCKLESCRIPT_TOOL = getIcon("/icons/bucklescriptTool.svg");
    public static final Icon DUNE_TOOL = getIcon("/icons/duneTool.svg");
    public static final Icon ESY_TOOL = getIcon("/icons/esyTool.svg");

    // GUTTER

    public static final Icon IMPLEMENTED = getIcon("/gutter/implementedMethod.svg");
    public static final Icon IMPLEMENTING = getIcon("/gutter/implementingMethod.svg");

    // OTHER

    public static final Icon DUNE = getIcon("/icons/duneLogo.svg");
    public static final Icon ESY = getIcon("/icons/esy.svg");

    public static final Icon BS_FILE = getIcon("/icons/bsFile.svg");
    public static final Icon ESY_FILE = getIcon("/icons/esyFile.svg");
    public static final Icon RML_FILE = getIcon("/icons/reFile.svg");
    public static final Icon RML_INTERFACE_FILE = getIcon("/icons/reiFile.svg");
    public static final Icon OCL_FILE = getIcon("/icons/mlFile.svg");
    public static final Icon OCL_INTERFACE_FILE = getIcon("/icons/mliFile.png");
    public static final Icon DUNE_FILE = getIcon("/icons/duneFile.svg");

    public static final Icon RML_FILE_MODULE = getIcon("/icons/reasonRed.svg");
    public static final Icon RML_FILE_MODULE_INTERFACE = getIcon("/icons/reasonBlue.svg");
    public static final Icon OCL_FILE_MODULE = getIcon("/icons/ocamlLogo.svg");
    public static final Icon OCL_FILE_MODULE_INTERFACE = getIcon("/icons/ocamlBlue.png");

    public static final Icon RML_BLUE = getIcon("/icons/reasonBlue.svg");
    public static final Icon RML_YELLOW = getIcon("/icons/reasonYellow.svg");

    public static final Icon OCL_MODULE = getIcon("/icons/ocamlModule.svg");
    public static final Icon OCL_SDK = getIcon("/icons/ocamlSdk.svg");
    public static final Icon OCL_BLUE_FILE = getIcon("/icons/ocamlBlue.png");
    public static final Icon OCL_GREEN_FILE = getIcon("/icons/ocamlGreen.png");

    public static final Icon TYPE = getIcon("/icons/type.svg");
    public static final Icon VARIANT = getIcon("/icons/variant.svg");
    public static final Icon ANNOTATION = getIcon("/nodes/annotationtype.svg");

    public static final Icon MODULE = getIcon("/nodes/javaModule.svg");
    public static final Icon MODULE_TYPE = getIcon("/icons/javaModuleType.svg");
    public static final Icon FUNCTOR = getIcon("/nodes/artifact.svg");
    public static final Icon LET = getIcon("/nodes/variable.svg");
    public static final Icon VAL = getIcon("/nodes/variable.svg");
    public static final Icon ATTRIBUTE = getIcon("/css/property.png");
    public static final Icon FUNCTION = getIcon("/nodes/function.svg");
    public static final Icon METHOD = getIcon("/nodes/method.svg");
    public static final Icon CLASS = getIcon("/nodes/class.svg");
    public static final Icon EXCEPTION = getIcon("/nodes/exceptionClass.svg");
    public static final Icon EXTERNAL = getIcon("/nodes/enum.svg");
    public static final Icon OBJECT = getIcon("/json/object.svg");

    public static final Icon VIRTUAL_NAMESPACE = getIcon("/actions/GroupByPackage.svg");
    public static final Icon OPEN = getIcon("/objectBrowser/showModules.png");
    public static final Icon INCLUDE = getIcon("/objectBrowser/showModules.png");

    public static final Icon OVERLAY_MANDATORY = getIcon("/ide/errorPoint.svg");
    public static final Icon OVERLAY_EXECUTE = getIcon("/general/comboArrowRight.png");
}
