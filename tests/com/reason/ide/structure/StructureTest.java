package com.reason.ide.structure;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.FileBase;
import icons.ORIcons;
import javax.swing.*;
import org.jetbrains.annotations.Nullable;

public class StructureTest extends ORBasePlatformTestCase {
  public void test_OCL_let() {
    FileBase a = configureCode("A.ml", "let x = 1");
    StructureViewModel model = new ORStructureViewModel(a);

    TreeElement x = model.getRoot().getChildren()[0];
    ItemPresentation pres = x.getPresentation();
    assertEquals("x", pres.getPresentableText());
  }

  public void test_OCL_val() {
    FileBase a = configureCode("A.mli", "val x: int");
    StructureViewModel model = new ORStructureViewModel(a);

    TreeElement x = model.getRoot().getChildren()[0];
    assertPresentation("x", "int", ORIcons.VAL, x.getPresentation());
  }

  public void test_OCL_type_record() {
    FileBase a = configureCode("A.ml", "type x = { a: int; b: string list }");
    StructureViewModel model = new ORStructureViewModel(a);

    TreeElement e = model.getRoot().getChildren()[0];
    assertPresentation("x", null, ORIcons.TYPE, e.getPresentation());
    TreeElement c1 = e.getChildren()[0];
    assertPresentation("a", "int", ORIcons.VAL, c1.getPresentation());
    TreeElement c2 = e.getChildren()[1];
    assertPresentation("b", "string list", ORIcons.VAL, c2.getPresentation());
  }

  public void test_OCL_module_type() {
    FileBase a = configureCode("A.ml", "module type I = sig val x : bool end");
    StructureViewModel model = new ORStructureViewModel(a);

    TreeElement i = model.getRoot().getChildren()[0];
    assertPresentation("I", "", ORIcons.MODULE_TYPE, i.getPresentation());
    TreeElement x = i.getChildren()[0];
    assertPresentation("x", "bool", ORIcons.VAL, x.getPresentation());
  }

  public void test_OCL_module_type_extraction() {
    configureCode("A.mli", "module type S = sig\n end");
    FileBase b = configureCode("B.ml", "module X : module type of A.S");
    StructureViewModel model = new ORStructureViewModel(b);

    TreeElement e = model.getRoot().getChildren()[0];
    assertPresentation("X", "", ORIcons.INNER_MODULE, e.getPresentation());
    TreeElement ee = e.getChildren()[0];
    assertPresentation("S", "A.mli", ORIcons.MODULE_TYPE, ee.getPresentation());
  }

  public void test_OCL_module_type_extraction_functor() {
    configureCode(
        "A.mli",
        "module type S = sig\n module Branch : sig type t end\n end\n module Make() : S\n module Vcs = Make()");
    FileBase b = configureCode("B.ml", "module X : module type of A.Vcs.Branch");
    StructureViewModel model = new ORStructureViewModel(b);

    TreeElement e = model.getRoot().getChildren()[0];
    assertPresentation("X", "", ORIcons.INNER_MODULE, e.getPresentation());
    TreeElement ee = e.getChildren()[0];
    assertPresentation("A.Vcs.Branch", "", ORIcons.MODULE_TYPE, ee.getPresentation());
  }

  // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/274
  // omit () in structure panel
  public void test_GH_274() {
    FileBase a = configureCode("A.ml", "let () = 1 + 2");
    StructureViewModel model = new ORStructureViewModel(a);

    assertEmpty(model.getRoot().getChildren());
  }

  private void assertPresentation(
      String name, String location, @Nullable Icon icon, ItemPresentation pres) {
    assertEquals("Incorrect name", name, pres.getPresentableText());
    assertEquals("Incorrect location", location, pres.getLocationString());
    assertEquals("Incorrect icon", icon, pres.getIcon(false));
  }
}
