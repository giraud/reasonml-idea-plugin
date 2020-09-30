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

  public void test_OCL_module_type() {
    FileBase a = configureCode("A.ml", "module type I = sig val x : bool end");
    StructureViewModel model = new ORStructureViewModel(a);

    TreeElement i = model.getRoot().getChildren()[0];
    assertPresentation("I", "", ORIcons.MODULE_TYPE, i.getPresentation());
    TreeElement x = i.getChildren()[0];
    assertPresentation("x", "bool", ORIcons.VAL, x.getPresentation());
  }

  public void test_OCL_module_type_extraction() {
    configureCode(
        "A.mli",
        "module type S = sig\n module Branch : sig type t end\n end\n module Make() : S\n module Vcs = Make()");
    FileBase a = configureCode("B.ml", "module type of A.Vcs");
    StructureViewModel model = new ORStructureViewModel(a);

    TreeElement i = model.getRoot().getChildren()[0];
    assertPresentation("Vcs", "A.mli", ORIcons.MODULE_TYPE, i.getPresentation());
    TreeElement x = i.getChildren()[0];
    assertPresentation("x", "bool", ORIcons.VAL, x.getPresentation());
  }

  private void assertPresentation(
      String name, String location, @Nullable Icon icon, ItemPresentation pres) {
    assertEquals("Incorrect name", name, pres.getPresentableText());
    assertEquals("Incorrect location", location, pres.getLocationString());
    assertEquals("Incorrect icon", icon, pres.getIcon(false));
  }
}
