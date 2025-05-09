package com.reason.ide.structure;

import com.intellij.ide.structureView.*;
import com.intellij.ide.util.treeView.smartTree.*;
import com.intellij.navigation.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;
import org.junit.*;

import javax.swing.*;

public class ProjectStructure_OCL_Test extends ORBasePlatformTestCase {
    @Test
    public void test_let() {
        FileBase a = configureCode("A.ml", "let x = 1");
        StructureViewModel model = new ORStructureViewModel(a);

        TreeElement x = model.getRoot().getChildren()[0];
        ItemPresentation pres = x.getPresentation();
        assertEquals("x", pres.getPresentableText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/439
    @Test
    public void test_let_underscore() {
        FileBase a = configureCode("A.ml", "let _ = ()");
        StructureViewModel model = new ORStructureViewModel(a);

        assertEmpty(model.getRoot().getChildren());
    }

    @Test
    public void test_deconstruction() {
        FileBase a = configureCode("A.ml", "let (a, b) = x");
        StructureViewModel model = new ORStructureViewModel(a);

        TreeElement[] children = model.getRoot().getChildren();
        assertSize(2, children);
        assertPresentation("a", "", ORIcons.LET, children[0].getPresentation());
        assertPresentation("b", "", ORIcons.LET, children[1].getPresentation());
    }

    @Test
    public void test_val() {
        FileBase a = configureCode("A.mli", "val x: int");
        StructureViewModel model = new ORStructureViewModel(a);

        TreeElement x = model.getRoot().getChildren()[0];
        assertPresentation("x", "int", ORIcons.VAL, x.getPresentation());
    }

    @Test
    public void test_type_record() {
        FileBase a = configureCode("A.ml", "type x = { a: int; mutable b: string list }");
        StructureViewModel model = new ORStructureViewModel(a);

        TreeElement e = model.getRoot().getChildren()[0];
        assertPresentation("x", null, ORIcons.TYPE, e.getPresentation());
        TreeElement c1 = e.getChildren()[0];
        assertPresentation("a", "int", ORIcons.VAL, c1.getPresentation());
        TreeElement c2 = e.getChildren()[1];
        assertPresentation("b", "string list", ORIcons.VAL, c2.getPresentation());
    }

    @Test
    public void test_module_type() {
        FileBase a = configureCode("A.ml", "module type I = sig val x : bool end");
        StructureViewModel model = new ORStructureViewModel(a);

        TreeElement i = model.getRoot().getChildren()[0];
        assertPresentation("I", "A.I", ORIcons.MODULE_TYPE, i.getPresentation());
        TreeElement x = i.getChildren()[0];
        assertPresentation("x", "bool", ORIcons.VAL, x.getPresentation());
    }

    @Test
    public void test_module_type_extraction() {
        configureCode("A.mli", "module type S = sig\n end");
        FileBase b = configureCode("B.ml", "module X : module type of A.S");
        StructureViewModel model = new ORStructureViewModel(b);

        TreeElement e = model.getRoot().getChildren()[0];
        assertPresentation("X", "B.X", ORIcons.INNER_MODULE, e.getPresentation());
        TreeElement ee = e.getChildren()[0];
        assertPresentation("S", "", ORIcons.MODULE_TYPE, ee.getPresentation());
    }

    @Test
    public void test_module_type_extraction_functor() {
        configureCode("A.mli", "module type S = sig\n module Branch : sig type t end\n end\n module Make() : S\n module Vcs = Make()");
        FileBase b = configureCode("B.ml", "module X : module type of A.Vcs.Branch");
        StructureViewModel model = new ORStructureViewModel(b);

        TreeElement e = model.getRoot().getChildren()[0];
        assertPresentation("X", "B.X", ORIcons.INNER_MODULE, e.getPresentation());
        TreeElement ee = e.getChildren()[0];
        assertPresentation("Branch", "", ORIcons.MODULE_TYPE, ee.getPresentation());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/274
    // omit () in structure panel
    @Test
    public void test_GH_274() {
        FileBase a = configureCode("A.ml", "let () = 1 + 2");
        StructureViewModel model = new ORStructureViewModel(a);

        assertEmpty(model.getRoot().getChildren());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/190
    // nested functions
    @Test
    public void test_GH_190() {
        FileBase e = configureCode("A.ml", "let fn a b = let open Pp in let fn1 = 1 in let fn2 = 2");
        StructureViewModel model = new ORStructureViewModel(e);

        TreeElement fn = model.getRoot().getChildren()[0];
        assertPresentation("fn", null, ORIcons.FUNCTION, fn.getPresentation());
        TreeElement fn1 = fn.getChildren()[0];
        assertPresentation("fn1", null, ORIcons.LET, fn1.getPresentation());
        TreeElement fn2 = fn.getChildren()[1];
        assertPresentation("fn2", null, ORIcons.LET, fn2.getPresentation());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/407
    @Test
    public void test_GH_407() {
        FileBase e = configureCode("A.ml", "let (!!) r = !r");
        StructureViewModel model = new ORStructureViewModel(e);

        TreeElement fn = model.getRoot().getChildren()[0];
        assertPresentation("(!!)", null, ORIcons.LET, fn.getPresentation());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/408
    @Test
    public void test_GH_408() {
        FileBase e = configureCode("A.ml", """
                let a =
                  let x = 0 in
                  x
                """);
        StructureViewModel model = new ORStructureViewModel(e);

        TreeElement fn = model.getRoot().getChildren()[0];
        assertPresentation("a", null, ORIcons.LET, fn.getPresentation());
        TreeElement fn1 = fn.getChildren()[0];
        assertPresentation("x", null, ORIcons.LET, fn1.getPresentation());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/429
    @Test
    public void test_GH_429() {
        FileBase e = configureCode("A.mli", """
                module X : sig
                    type t
                end
                """);

        StructureViewModel model = new ORStructureViewModel(e);

        TreeElement x = model.getRoot().getChildren()[0];
        assertPresentation("X", "A.X", ORIcons.INNER_MODULE_INTF, x.getPresentation());
        TreeElement xt = x.getChildren()[0];
        assertPresentation("t", null, ORIcons.TYPE, xt.getPresentation());

    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/490
    @Test
    public void test_GH_490() {
        FileBase e = configureCode("A.ml", """
                let o = object(self)
                  val mutable v1 = 1
                  method m1 = () => ()
                end
                """);

        StructureViewModel model = new ORStructureViewModel(e);

        StructureViewElement.StructureObjectView o = (StructureViewElement.StructureObjectView) model.getRoot().getChildren()[0];
        assertPresentation("o", null, ORIcons.CLASS, o.getPresentation());
    }


    // https://github.com/giraud/reasonml-idea-plugin/issues/490
    @Test
    public void test_GH_490_factory() {
        FileBase e = configureCode("A.ml", """
                let segment_model (doc : sentence Doc.document) : Wg_Segment.model =
                object (self)
                    val mutable cbs = []
                end
                """);

        StructureViewModel model = new ORStructureViewModel(e);

        StructureViewElement.StructureObjectView o = (StructureViewElement.StructureObjectView) model.getRoot().getChildren()[0];
        assertPresentation("segment_model", null, ORIcons.CLASS, o.getPresentation());
    }

    private void assertPresentation(String name, String location, @Nullable Icon icon, @NotNull ItemPresentation pres) {
        assertEquals("Incorrect name", name, pres.getPresentableText());
        assertEquals("Incorrect location", location, pres.getLocationString());
        assertEquals("Incorrect icon", icon, pres.getIcon(false));
    }
}
