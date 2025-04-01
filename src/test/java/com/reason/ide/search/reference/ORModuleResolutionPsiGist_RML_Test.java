package com.reason.ide.search.reference;

import com.intellij.psi.util.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

import static java.util.List.*;

@SuppressWarnings("ConstantConditions")
public class ORModuleResolutionPsiGist_RML_Test extends ORBasePlatformTestCase {
    @Test
    public void test_include_no_resolution() {
        FileBase e = configureCode("A.re", "module A1 = {}; include B.B1;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiInclude ei = PsiTreeUtil.findChildOfType(e, RPsiInclude.class);
        assertEmpty(data.getValues(ei));
        assertEmpty(data.getValues(e));
    }

    @Test
    public void test_open_no_resolution() {
        FileBase e = configureCode("A.re", "module A1 = {}; open B.B1;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiOpen eo = PsiTreeUtil.findChildOfType(e, RPsiOpen.class);
        assertEmpty(data.getValues(eo));
        assertEmpty(data.getValues(e));
    }

    @Test
    public void test_include_in_file() {
        FileBase e = configureCode("A.re", "module A1 = { module A2 = {}; }; include A1.A2;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiInclude ei = PsiTreeUtil.findChildOfType(e, RPsiInclude.class);
        assertOrderedEquals(data.getValues(ei/*A1.A2*/), "A.A1.A2");
        assertOrderedEquals(data.getValues(e/*A.re*/), "A.A1.A2");
    }

    @Test
    public void test_open_in_file() {
        FileBase e = configureCode("A.re", "module A1 = { module A2 = {}; }; open A1.A2;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiOpen eo = PsiTreeUtil.findChildOfType(e, RPsiOpen.class);
        assertOrderedEquals(data.getValues(eo/*A1.A11*/), "A.A1.A2");
        assertEmpty(data.getValues(e/*A.re*/));
    }

    @Test
    public void test_include_in_file_steps() {
        FileBase e = configureCode("A.re", "module A1 = { module A2 = {}; }; include A1; include A2;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        ArrayList<RPsiInclude> eis = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiInclude.class));
        assertOrderedEquals(data.getValues(eis.get(0/*A1*/)), "A.A1");
        assertOrderedEquals(data.getValues(eis.get(1/*A2*/)), "A.A1.A2");
        assertOrderedEquals(data.getValues(e/*A.re*/), "A.A1", "A.A1.A2");
    }

    @Test
    public void test_open_in_file_steps() {
        FileBase e = configureCode("A.re", "module A1 = { module A2 = {}; }; open A1; open A2;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        ArrayList<RPsiOpen> eos = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiOpen.class));
        assertOrderedEquals(data.getValues(eos.get(0/*A1*/)), "A.A1");
        assertOrderedEquals(data.getValues(eos.get(1/*A2*/)), "A.A1.A2");
        assertEmpty(data.getValues(e/*A.re*/));
    }

    @Test
    public void test_include_in_steps() {
        configureCode("A.re", "module A1 = { module A2 = {}; }");
        FileBase e = configureCode("B.re", "include A.A1; include A2;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        ArrayList<RPsiInclude> eis = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiInclude.class));
        assertEmpty(data.getValues(eis.get(0/*A.A1*/))); // Same
        assertOrderedEquals(data.getValues(eis.get(1/*A2*/)), "A.A1.A2");
        assertOrderedEquals(data.getValues(e/*B.re*/), "A.A1", "A.A1.A2");
    }

    @Test
    public void test_open_in_steps() {
        configureCode("A.re", "module A1 = { module A2 = {}; }");
        FileBase e = configureCode("B.re", "open A.A1; open A2;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        ArrayList<RPsiOpen> eos = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiOpen.class));
        assertEmpty(data.getValues(eos.get(0/*A.A1*/))); // Same
        assertOrderedEquals(data.getValues(eos.get(1/*A2*/)), "A.A1.A2");
        assertEmpty(data.getValues(e/*B*/));
    }

    @Test
    public void test_aliases_01() {
        FileBase e = configureCode("A.re", """
                module A1 = { module A2 = {}; };
                module B1 = A1;
                include B1;
                module B2 = A2;
                include B2;
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiInclude> eis = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiInclude.class));
        assertOrderedEquals(data.getValues(eis.get(0/*B1*/)), "A.A1");
        assertOrderedEquals(data.getValues(eis.get(1/*B2*/)), "A.A1.A2");

        List<RPsiModule> ems = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiModule.class));
        assertOrderedEquals(data.getValues(ems.get(2/*B1*/)), "A.A1");
        assertOrderedEquals(data.getValues(ems.get(3/*B2*/)), "A.A1.A2");

        assertOrderedEquals(data.getValues(e/*A*/), "A.A1", "A.A1.A2");
    }

    @Test
    public void test_aliases_02() {
        FileBase e = configureCode("A.re", "module A1 = { module A2 = {}; module B2 = A2; };" +
                "module B1 = A1;" +
                "include B1.B2;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiInclude ei = PsiTreeUtil.findChildOfType(e, RPsiInclude.class);
        assertOrderedEquals(data.getValues(ei/*B1.B2*/), "A.A1.A2");
        assertOrderedEquals(data.getValues(e/*A*/), "A.A1.A2");
    }

    @Test
    public void test_aliases_03() {
        configureCode("A.re", "");
        FileBase e = configureCode("B.re", "module B1 = A;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = PsiTreeUtil.findChildOfType(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em/*B1*/), "A");
        assertEmpty(data.getValues(e/*B*/));
    }

    @Test
    public void test_aliases_04() {
        configureCode("A.re", "module W = { module X = { module Y = { module Z = { let z = 1; }; }; }; };");
        FileBase e = configureCode("B.re", "module C = A.W.X; module D = C.Y.Z;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiModule> ems = copyOf(PsiTreeUtil.findChildrenOfType(e, RPsiModule.class));
        assertOrderedEquals(data.getValues(ems.get(0/*C*/)), "A.W.X");
        assertOrderedEquals(data.getValues(ems.get(1/*D*/)), "A.W.X.Y.Z");
        assertEmpty(data.getValues(e/*B*/));
    }

    @Test
    public void test_aliases_05() {
        configureCode("A.re", "module A1 = { module A11 = { type id = string; }; };");
        configureCode("B.re", "module B1 = A.A1;");
        FileBase e = configureCode("Dummy.re", "module X = B.B1.A11;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = PsiTreeUtil.findChildOfType(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em/*ELayout*/), "A.A1.A11");
    }

    @Test
    public void test_alias_of_alias() {
        configureCode("A.re", """
                module A1 = {
                    module A2 = { let id = "_new_"; };
                };
                """);

        configureCode("B.re", """
                module B1 = {
                  module B2 = {
                    module B3 = { let id = A.A1.A2.id; };
                  };
                };
                
                module B4 = {
                  include A;
                  module B5 = B1.B2;
                };
                """);

        FileBase e = configureCode("C.res", """
                module C1 = B.B4;
                module C2 = C1.B5.B3;
                let _ = C2.id;
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiModule> ems = copyOf(PsiTreeUtil.findChildrenOfType(e, RPsiModule.class));
        assertOrderedEquals(data.getValues(ems.get(0)/*C1*/), "B.B4", "A");
        assertOrderedEquals(data.getValues(ems.get(1)/*C2*/), "B.B1.B2.B3");
    }

    @Test
    public void test_alias_of_alias_02() {
        configureCode("A.re", """
                module A1 = {
                    module A2 = { };
                };
                """);
        configureCode("B.re", """
                module B1 = {
                  include A;
                };
                """);
        FileBase e = configureCode("C.re", """
                module C1 = B.B1;
                module C2 = C1.A1;
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiModule> ems = copyOf(PsiTreeUtil.findChildrenOfType(e, RPsiModule.class));
        assertOrderedEquals(data.getValues(ems.get(0)/*C1*/), "B.B1", "A");
        assertOrderedEquals(data.getValues(ems.get(1)/*C2*/), "A.A1");
    }

    @Test
    public void test_alias_of_alias_03() {
        configureCode("A.re", "module A1 = {};");
        configureCode("B.re", "include A;");
        FileBase e = configureCode("C.re", """
                module C1 = B;
                module C2 = C1.A1;
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiModule> ems = copyOf(PsiTreeUtil.findChildrenOfType(e, RPsiModule.class));
        assertOrderedEquals(data.getValues(ems.get(0)/*C1*/), "B", "A");
        assertOrderedEquals(data.getValues(ems.get(1)/*C2*/), "A.A1");
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/426
    @Test
    public void test_GH_426_aliases_same_file() {
        FileBase e = configureCode("A.re", """
                module W = { module X = { module Y = { module Z = {}; }; }; };
                module C = W.X;
                module D = C.Y.Z;
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule[] ems = PsiTreeUtil.getChildrenOfType(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(ems[1/*C*/]), "A.W.X");
        assertOrderedEquals(data.getValues(ems[2/*D*/]), "A.W.X.Y.Z");
        assertEmpty(data.getValues(e/*B*/));
    }

    @Test
    public void test_include_in_module() {
        configureCode("A.re", "");
        FileBase e = configureCode("B.re", "module B1 = { include A; };");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = PsiTreeUtil.findChildOfType(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em/*B.B1*/), "A");
    }

    @Test
    public void test_same_includes() {
        FileBase e = configureCode("A.re", "module A1 = { module A2 = {}; }; open A1; include A2; module A2 = {}; include A2;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        ArrayList<RPsiInclude> eis = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiInclude.class));
        assertOrderedEquals(data.getValues(eis.get(0)/*A2*/), "A.A1.A2");
        assertOrderedEquals(data.getValues(eis.get(1)/*A2*/), "A.A2");
    }

    @Test
    public void test_functor_in_file() {
        FileBase e = configureCode("A.re", "module type S = { module P: {}; };" +
                "module F = () : S => { module P = {}; };" +
                "module M = F({});");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiModule> ems = ORUtil.findImmediateChildrenOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(ems.get(1)/*F*/), "A.S");
        assertOrderedEquals(data.getValues(ems.get(2)/*M*/), "A.F");
    }

    @Test
    public void test_functor_result_out_file() {
        configureCode("A.re", "module type Result = { let a: int; };");
        FileBase e = configureCode("B.re", "module T = A; module Make = (M:Intf): T.Result => { let b = 3; };");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiModule> ems = ORUtil.findImmediateChildrenOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(ems.get(1)/*Make*/), "A.Result");
    }

    @Test
    public void test_functor_no_signature() {
        FileBase e = configureCode("A.re", "module M = () => { let x = true; }; module X = M();");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiModule> ems = ORUtil.findImmediateChildrenOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(ems.get(1)/*M*/), "A.M");
    }

    @Test
    public void test_functor_outside() {
        configureCode("A.re", "module type S = { module P: {}; }; module F = () : S => { module P = {}; };");
        FileBase e = configureCode("B.re", "module M = A.F({});");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = ORUtil.findImmediateFirstChildOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em/*M*/), "A.F");
        assertEmpty(data.getValues(e));
    }

    @Test
    public void test_functor_open() {
        configureCode("A.re", "module type Intf = { let x: bool; }; module MakeIntf = (I:Intf) : Intf => { let y = 1; };");
        FileBase e = configureCode("B.re", "open A; module Instance = MakeIntf({let x = true});");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = ORUtil.findImmediateFirstChildOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em/*Instance*/), "A.MakeIntf");
        assertEmpty(data.getValues(e));
    }

    @Test
    public void test_functor_instance_same_file() {
        configureCode("B.re", "module type Result = { let a: int; };");
        FileBase e = configureCode("A.re", """
                module Make = (M:Intf): (B.Result with type t := M.t) => {};
                module Instance = Make({});
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);
        List<RPsiModule> ems = copyOf(PsiTreeUtil.findChildrenOfType(e, RPsiModule.class));

        assertOrderedEquals(data.getValues(ems.get(1)/*Instance*/), "A.Make");
    }

    @Test
    public void test_file_include_functor() {
        FileBase e = configureCode("A.re", "module Make = () => { let y = 1; }; include Make();");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        assertOrderedEquals(data.getValues(e), "A.Make");
    }

    @Test
    public void test_functor_path() {
        configureCode("D.re", """
                module type D1Intf = {
                  type t;
                };
                """);
        configureCode("C.re", """
                module type C1Intf = {
                  let make: unit => string;
                };
                
                module Make = (MX: D.D1Intf): C1Intf => {
                  let make = () => "";
                };
                """);
        configureCode("B.re", "module B1 = C;");
        FileBase e = configureCode("A.re", "module Instance = B.B1.Make(X);");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);
        RPsiModule em = PsiTreeUtil.findChildOfType(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em/*Instance*/), "C.Make");
    }

    @Test
    public void test_module_type_inside() {
        FileBase e = configureCode("A.re", """
                module type S = {};
                module M : S = {};
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModuleSignature emt = PsiTreeUtil.findChildOfType(e, RPsiModuleSignature.class);
        assertOrderedEquals(data.getValues(emt/*S*/), "A.S");
    }

    @Test
    public void test_module_type_open_alias() {
        FileBase e = configureCode("A.re", """
                module A1 = {
                  module type S = {};
                };
                module X = A1;
                open X;
                module M: S = {};
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModuleSignature emt = PsiTreeUtil.findChildOfType(e, RPsiModuleSignature.class);
        assertOrderedEquals(data.getValues(emt/*S*/), "A.A1.S");
    }

    @Test
    public void test_module_type_outside() {
        configureCode("A.re", "module type S = {};");
        FileBase e = configureCode("B.re", "module M: A.S = {};");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModuleSignature emt = PsiTreeUtil.findChildOfType(e, RPsiModuleSignature.class);
        assertOrderedEquals(data.getValues(emt/*S*/), "A.S");
    }

    @Test
    public void test_module_type_deep() {
        FileBase e = configureCode("A.re", """
                module B = {
                  module type Intf = {};
                };
                module Impl : B.Intf = {};
                """);
        RPsiModuleSignature es = PsiTreeUtil.findChildOfType(e, RPsiModuleSignature.class);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);
        assertOrderedEquals(data.getValues(es/*B.Intf*/), "A.B.Intf");

    }

    @Test
    public void test_tag_inside() {
        FileBase e = configureCode("A.re", "module X = { [@react.component] let make = (~value) => <div/>; }; <X value=1></X>;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiTag et = ORUtil.findImmediateFirstChildOfClass(e, RPsiTag.class);
        assertOrderedEquals(data.getValues(et.getFirstChild()/*X*/), "A.X");
    }

    @Test
    public void test_tag_outside() {
        configureCode("X.re", "[@react.component] let make = (~value) => <div/>;");
        FileBase e = configureCode("A.re", "<X value=1></X>;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiTagStart et = PsiTreeUtil.findChildOfType(e, RPsiTagStart.class);
        assertOrderedEquals(data.getValues(et/*X*/), "X");
    }

    @Test
    public void test_tag_open_outside() {
        configureCode("X.re", "module Y = { [@react.component] let make = (~value) => <div/>; }");
        FileBase e = configureCode("A.re", "open X; <Y value=1></Y>;");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiTagStart et = PsiTreeUtil.findChildOfType(e, RPsiTagStart.class);
        assertOrderedEquals(data.getValues(et/*Y*/), "X.Y");
    }

    @Test
    public void test_unpack_local() {
        FileBase e = configureCode("A.re", """
                module type I = { let x: int; };
                module Three: I = { let x = 3; };
                let three: module I = (module Three);
                module New_three = (val three: I);
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = ORUtil.findImmediateLastChildOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em), "A.I");
    }

    @Test
    public void test_unpack_open() {
        configureCode("A.re", """
                module type I = { let x: int; };
                module Three: I = { let x = 3; };
                """);
        FileBase e = configureCode("B.re", """
                open A;
                let three: module I = (module Three);
                module New_three = (val three: I);
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = ORUtil.findImmediateLastChildOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em), "A.I");
    }

    @Test
    public void test_unpack_no_signature() {
        configureCode("A.re", """
                module type I = { let x: int; };
                module Three: I = { let x = 3; };
                """);
        FileBase e = configureCode("B.re", """
                open A;
                let three: module I = (module Three);
                module New_three = (val three);
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = ORUtil.findImmediateFirstChildOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em), "A.I");
    }

    @Test
    public void test_unpack_no_signature_qname() {
        configureCode("A.re", """
                module A1 = {
                  module type I = { let x: int; };
                };
                module Three : A1.I = { let x = 3 };
                """);
        FileBase e = configureCode("B.re", """
                module B1 = A;
                let three: module B1.A1.I = (module B1.Three);
                module New_three = (val three);
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = ORUtil.findImmediateLastChildOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em), "A.A1.I");
    }

    @Test
    public void test_unpack_parameter() {
        FileBase e = configureCode("A.re", """
                module type I = { let x: int; };
                let x = (~p: (module I)) => { module S = (val p); };
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = PsiTreeUtil.findChildOfType(PsiTreeUtil.getChildOfType(e, RPsiLet.class).getBinding(), RPsiModule.class);
        assertOrderedEquals(data.getValues(em), "A.I");
    }

    @Test
    public void test_unpack_parameter_global_module() {
        configureCode("A.re", """
                module B = {
                  module type I = {
                    let fn: int => unit;
                  };
                };
                """);
        FileBase e = configureCode("C.re", "let x = (~p: (module A.B.I)) => { module S = (val p); };");


        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = PsiTreeUtil.findChildOfType(PsiTreeUtil.getChildOfType(e, RPsiLet.class).getBinding(), RPsiModule.class);
        assertOrderedEquals(data.getValues(em), "A.B.I");
    }
}
