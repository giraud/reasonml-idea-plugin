package com.reason.lang.ocaml;

import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;

@SuppressWarnings("ConstantConditions")
public class CommentParsingTest extends BaseParsingTestCase {
  public CommentParsingTest() {
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
}
