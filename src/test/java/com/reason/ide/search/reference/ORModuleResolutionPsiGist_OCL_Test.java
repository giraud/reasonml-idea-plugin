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
public class ORModuleResolutionPsiGist_OCL_Test extends ORBasePlatformTestCase {
    @Test
    public void test_include_no_resolution() {
        FileBase e = configureCode("A.ml", """
                module A1 = struct end
                include B.B1
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiInclude ei = PsiTreeUtil.findChildOfType(e, RPsiInclude.class);
        assertEmpty(data.getValues(ei));
        assertEmpty(data.getValues(e));
    }

    @Test
    public void test_open_no_resolution() {
        FileBase e = configureCode("A.ml", """
                module A1 = struct end
                open B.B1
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiOpen eo = PsiTreeUtil.findChildOfType(e, RPsiOpen.class);
        assertEmpty(data.getValues(eo));
        assertEmpty(data.getValues(e));
    }

    @Test
    public void test_include_in_file() {
        FileBase e = configureCode("A.ml", """
                module A1 = struct
                  module A2 = struct end
                end
                include A1.A2
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiInclude ei = PsiTreeUtil.findChildOfType(e, RPsiInclude.class);
        assertOrderedEquals(data.getValues(ei/*A1.A2*/), "A.A1.A2");
        assertOrderedEquals(data.getValues(e/*A.re*/), "A.A1.A2");
    }

    @Test
    public void test_open_in_file() {
        FileBase e = configureCode("A.ml", """
                module A1 = struct
                  module A2 = struct end
                end
                open A1.A2
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiOpen eo = PsiTreeUtil.findChildOfType(e, RPsiOpen.class);
        assertOrderedEquals(data.getValues(eo/*A1.A11*/), "A.A1.A2");
        assertEmpty(data.getValues(e/*A.re*/));
    }

    @Test
    public void test_include_in_file_steps() {
        FileBase e = configureCode("A.ml", """
                module A1 = struct
                  module A2 = struct end
                end
                include A1
                include A2
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        ArrayList<RPsiInclude> eis = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiInclude.class));
        assertOrderedEquals(data.getValues(eis.get(0/*A1*/)), "A.A1");
        assertOrderedEquals(data.getValues(eis.get(1/*A2*/)), "A.A1.A2");
        assertOrderedEquals(data.getValues(e/*A.re*/), "A.A1", "A.A1.A2");
    }

    @Test
    public void test_open_in_file_steps() {
        FileBase e = configureCode("A.ml", """
                module A1 = struct
                  module A2 = struct end
                end
                open A1
                open A2
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        ArrayList<RPsiOpen> eos = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiOpen.class));
        assertOrderedEquals(data.getValues(eos.get(0/*A1*/)), "A.A1");
        assertOrderedEquals(data.getValues(eos.get(1/*A2*/)), "A.A1.A2");
        assertEmpty(data.getValues(e/*A.re*/));
    }

    @Test
    public void test_include_in_steps() {
        configureCode("A.ml", """
                module A1 = struct
                  module A2 = struct end
                end
                """);
        FileBase e = configureCode("B.ml", """
                include A.A1
                include A2
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        ArrayList<RPsiInclude> eis = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiInclude.class));
        assertEmpty(data.getValues(eis.get(0/*A.A1*/))); // Same
        assertOrderedEquals(data.getValues(eis.get(1/*A2*/)), "A.A1.A2");
        assertOrderedEquals(data.getValues(e/*B.re*/), "A.A1", "A.A1.A2");
    }

    @Test
    public void test_open_in_steps() {
        configureCode("A.ml", """
                module A1 = struct
                  module A2 = struct end
                end
                """);
        FileBase e = configureCode("B.ml", """
                open A.A1
                open A2
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        ArrayList<RPsiOpen> eos = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiOpen.class));
        assertEmpty(data.getValues(eos.get(0/*A.A1*/))); // Same
        assertOrderedEquals(data.getValues(eos.get(1/*A2*/)), "A.A1.A2");
        assertEmpty(data.getValues(e/*B*/));
    }

    @Test
    public void test_aliases_01() {
        FileBase e = configureCode("A.ml", """
                module A1 = struct
                  module A2 = struct end
                end
                module B1 = A1
                include B1
                module B2 = A2
                include B2
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
        FileBase e = configureCode("A.ml", """
                module A1 = struct
                  module A2 = struct end
                  module B2 = A2
                end
                module B1 = A1
                include B1.B2
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiInclude ei = PsiTreeUtil.findChildOfType(e, RPsiInclude.class);
        assertOrderedEquals(data.getValues(ei/*B1.B2*/), "A.A1.A2");
        assertOrderedEquals(data.getValues(e/*A*/), "A.A1.A2");
    }

    @Test
    public void test_aliases_03() {
        configureCode("A.ml", "");
        FileBase e = configureCode("B.ml", "module B1 = A");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = PsiTreeUtil.findChildOfType(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em/*B1*/), "A");
        assertEmpty(data.getValues(e/*B*/));
    }

    @Test
    public void test_aliases_04() {
        configureCode("A.ml", """
                module W = struct
                  module X = struct
                    module Y = struct
                      module Z = struct
                        let z = 1
                      end
                    end
                  end
                end
                """);
        FileBase e = configureCode("B.ml", """
                module C = A.W.X
                module D = C.Y.Z
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiModule> ems = copyOf(PsiTreeUtil.findChildrenOfType(e, RPsiModule.class));
        assertOrderedEquals(data.getValues(ems.get(0/*C*/)), "A.W.X");
        assertOrderedEquals(data.getValues(ems.get(1/*D*/)), "A.W.X.Y.Z");
        assertEmpty(data.getValues(e/*B*/));
    }

    @Test
    public void test_aliases_05() {
        configureCode("A.ml", "module A1 = struct module A11 = struct type id = string end end");
        configureCode("B.ml", "module B1 = A.A1");
        FileBase e = configureCode("Dummy.ml", "module X = B.B1.A11");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = PsiTreeUtil.findChildOfType(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em/*ELayout*/), "A.A1.A11");
    }

    @Test
    public void test_alias_of_alias() {
        configureCode("A.ml", """
                module A1 = struct
                    module A2 = struct
                      let id = "_new_"
                    end
                end
                """);

        configureCode("B.ml", """
                module B1 = struct
                  module B2 = struct
                    module B3 = struct
                      let id = A.A1.A2.id
                    end
                  end
                end
                
                module B4 = struct
                  include A
                  module B5 = B1.B2
                end
                """);

        FileBase e = configureCode("C.ml", """
                module C1 = B.B4
                module C2 = C1.B5.B3
                let _ = C2.id
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiModule> ems = copyOf(PsiTreeUtil.findChildrenOfType(e, RPsiModule.class));
        assertOrderedEquals(data.getValues(ems.get(0)/*C1*/), "B.B4", "A");
        assertOrderedEquals(data.getValues(ems.get(1)/*C2*/), "B.B1.B2.B3");
    }

    @Test
    public void test_alias_of_alias_02() {
        configureCode("A.ml", """
                module A1 = struct
                    module A2 = struct end
                end
                """);
        configureCode("B.ml", """
                module B1 = struct
                  include A
                end
                """);
        FileBase e = configureCode("C.ml", """
                module C1 = B.B1
                module C2 = C1.A1
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiModule> ems = copyOf(PsiTreeUtil.findChildrenOfType(e, RPsiModule.class));
        assertOrderedEquals(data.getValues(ems.get(0)/*C1*/), "B.B1", "A");
        assertOrderedEquals(data.getValues(ems.get(1)/*C2*/), "A.A1");
    }

    @Test
    public void test_alias_of_alias_03() {
        configureCode("A.ml", "module A1 = struct end");
        configureCode("B.ml", "include A");
        FileBase e = configureCode("C.ml", """
                module C1 = B
                module C2 = C1.A1
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiModule> ems = copyOf(PsiTreeUtil.findChildrenOfType(e, RPsiModule.class));
        assertOrderedEquals(data.getValues(ems.get(0)/*C1*/), "B", "A");
        assertOrderedEquals(data.getValues(ems.get(1)/*C2*/), "A.A1");
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/426
    @Test
    public void test_GH_426_aliases_same_file() {
        FileBase e = configureCode("A.ml", """
                module W = struct
                  module X = struct
                    module Y = struct
                      module Z = struct
                        let z = 1
                      end
                    end
                  end
                end
                
                module C = W.X
                module D = C.Y.Z
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule[] ems = PsiTreeUtil.getChildrenOfType(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(ems[1/*C*/]), "A.W.X");
        assertOrderedEquals(data.getValues(ems[2/*D*/]), "A.W.X.Y.Z");
        assertEmpty(data.getValues(e/*B*/));
    }

    @Test
    public void test_include_in_module() {
        configureCode("A.ml", "");
        FileBase e = configureCode("B.ml", """
                module B1 = struct
                  include A
                end
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = PsiTreeUtil.findChildOfType(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em/*B.B1*/), "A");
    }

    @Test
    public void test_same_includes() {
        FileBase e = configureCode("A.ml", """
                module A1 = struct
                  module A2 = struct end
                end
                open A1
                include A2
                module A2 = struct end
                include A2
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        ArrayList<RPsiInclude> eis = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiInclude.class));
        assertOrderedEquals(data.getValues(eis.get(0)/*A2*/), "A.A1.A2");
        assertOrderedEquals(data.getValues(eis.get(1)/*A2*/), "A.A2");
    }

    @Test
    public void test_functor_in_file() {
        FileBase e = configureCode("A.ml", """
                module type S = sig
                  module P : sig end
                end
                module F() : S = struct
                  module P = struct end
                end
                module M = F(struct end)
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiModule> ems = ORUtil.findImmediateChildrenOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(ems.get(1)/*F*/), "A.S");
        assertOrderedEquals(data.getValues(ems.get(2)/*M*/), "A.F");
    }

    @Test
    public void test_functor_result_out_file() {
        configureCode("A.ml", "module type Result = sig val a: int end");
        FileBase e = configureCode("B.ml", """
                module T = A
                module Make(M:Intf) : T.Result = struct
                  let b = 3
                end
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiModule> ems = ORUtil.findImmediateChildrenOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(ems.get(1)/*Make*/), "A.Result");
    }

    @Test
    public void test_functor_no_signature() {
        FileBase e = configureCode("A.ml", """
                module M() = struct
                  let x = true
                end
                module X = M(struct end)
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        List<RPsiModule> ems = ORUtil.findImmediateChildrenOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(ems.get(1)/*M*/), "A.M");
    }

    @Test
    public void test_functor_outside() {
        configureCode("A.ml", """
                    module type S = sig
                      module P : sig end
                    end
                    module F() : S = struct
                      module P = struct end
                    end
                """);
        FileBase e = configureCode("B.ml", "module M = A.F(struct end)");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = ORUtil.findImmediateFirstChildOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em/*M*/), "A.F");
        assertEmpty(data.getValues(e));
    }

    @Test
    public void test_functor_open() {
        configureCode("A.ml", """
                module type Intf = sig
                  val x : bool
                end
                module MakeIntf(I:Intf) : Intf = struct
                  let y = 1
                end
                """);
        FileBase e = configureCode("B.ml", """
                open A
                module Instance = MakeIntf(struct let x = true end)
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = ORUtil.findImmediateFirstChildOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em/*Instance*/), "A.MakeIntf");
        assertEmpty(data.getValues(e));
    }

    @Test
    public void test_functor_instance_same_file() {
        configureCode("B.ml", "module type Result = sig val a: int end");
        FileBase e = configureCode("A.ml", """
                module Make(M:Intf) = (struct end : (B.Result with type t :=  M.t))
                module Instance = Make(struct end)
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);
        List<RPsiModule> ems = copyOf(PsiTreeUtil.findChildrenOfType(e, RPsiModule.class));

        assertOrderedEquals(data.getValues(ems.get(1)/*Instance*/), "A.Make");
    }

    @Test
    public void test_file_include_functor() {
        FileBase e = configureCode("A.ml", """
                module Make() = struct
                  let y = 1
                end
                include Make(struct end)
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        assertOrderedEquals(data.getValues(e), "A.Make");
    }

    @Test
    public void test_functor_path() {
        configureCode("D.ml", """
                module type D1Intf = sig
                  type t
                end
                """);
        configureCode("C.ml", """
                module type C1Intf = sig
                  val make: unit => string
                end
                
                module Make (MX: D.D1Intf): C1Intf = struct
                  let make () = ""
                end
                """);
        configureCode("B.ml", "module B1 = C");
        FileBase e = configureCode("A.ml", "module Instance = B.B1.Make(X)");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);
        RPsiModule em = PsiTreeUtil.findChildOfType(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em/*Instance*/), "C.Make");
    }

    @Test
    public void test_unpack_local() {
        FileBase e = configureCode("A.ml", """
                module type I = sig val x : int end
                module Three : I = struct let x = 3 end
                let three = (module Three : I)
                module New_three = (val three : I)
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = ORUtil.findImmediateLastChildOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em), "A.I");
    }

    @Test
    public void test_unpack_open() {
        configureCode("A.ml", """
                module type I = sig val x : int end
                module Three : I = struct let x = 3 end
                """);
        FileBase e = configureCode("B.ml", """
                open A
                let three = (module Three : I)
                module New_three = (val three : I)
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = ORUtil.findImmediateFirstChildOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em), "A.I");
    }

    @Test
    public void test_unpack_no_signature() {
        configureCode("A.ml", """
                module type I = sig val x : int end
                module Three : I = struct let x = 3 end
                """);
        FileBase e = configureCode("B.ml", """
                open A
                let three = (module Three : I)
                module New_three = (val three)
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = ORUtil.findImmediateFirstChildOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em), "A.I");
    }

    @Test
    public void test_unpack_no_signature_qname() {
        configureCode("A.ml", """
                module A1 = struct
                  module type I = sig val x : int end
                end
                module Three : A1.I = struct let x = 3 end
                """);
        FileBase e = configureCode("B.ml", """
                module B1 = A
                let three = (module B1.Three : B1.A1.I)
                module New_three = (val three)
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = ORUtil.findImmediateLastChildOfClass(e, RPsiModule.class);
        assertOrderedEquals(data.getValues(em), "A.A1.I");
    }

    @Test
    public void test_unpack_parameter() {
        FileBase e = configureCode("A.ml", """
                module type I = sig val x: int end
                let x ~p:(p: (module I)) = let module S = (val p) in S.x
                """);

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiLetBinding el = PsiTreeUtil.getChildOfType(e, RPsiLet.class).getBinding();
        RPsiModule em = PsiTreeUtil.findChildOfType(el, RPsiModule.class);
        assertOrderedEquals(data.getValues(em), "A.I");
    }

    @Test
    public void test_unpack_parameter_global_module() {
        configureCode("A.ml", """
                module B = struct
                  module type I = sig
                    val fn: int => unit
                  end
                end
                """);
        FileBase e = configureCode("C.ml", "let x ~p:(p:(module A.B.I)) = let module S = (val p) in S.fn");

        ORModuleResolutionPsiGist.Data data = ORModuleResolutionPsiGist.getData(e);

        RPsiModule em = PsiTreeUtil.findChildOfType(PsiTreeUtil.getChildOfType(e, RPsiLet.class).getBinding(), RPsiModule.class);
        assertOrderedEquals(data.getValues(em), "A.B.I");
    }
}
