package com.reason.lang.ocaml;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;

@SuppressWarnings("ConstantConditions")
public class CommentTest extends BaseParsingTestCase {
  public CommentTest() {
    super("", "ml", new OclParserDefinition());
  }

  public void testConstant() {
    PsiFile psiFile = parseCode("(* *)");
    assertInstanceOf(firstElement(psiFile), PsiComment.class);
  }

  public void testConstant2() {
    FileBase psiFile = parseCode("(* \"this is a string *)\" *)");
    assertInstanceOf(firstElement(psiFile), PsiComment.class);
    assertEquals(1, childrenCount(psiFile));
  }

  public void testConstant3() {
    FileBase psiFile = parseCode("(* \"this is a string *)\" \"(* this is a string too*)\" *)");
    assertInstanceOf(firstElement(psiFile), PsiComment.class);
    assertEquals(1, childrenCount(psiFile));
  }

  public void testConstant4() {
    FileBase psiFile = parseCode("(* (* this is a string too*) *)");
    assertInstanceOf(firstElement(psiFile), PsiComment.class);
    assertEquals(2, childrenCount(psiFile));
  }
}
