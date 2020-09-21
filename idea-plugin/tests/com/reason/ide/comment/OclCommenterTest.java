package com.reason.ide.comment;

import com.intellij.codeInsight.generation.actions.CommentByBlockCommentAction;
import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.reason.ide.files.OclFileType;

public class OclCommenterTest extends LightPlatformCodeInsightFixtureTestCase {

  public void testLineCommenter() {
    myFixture.configureByText(OclFileType.INSTANCE, "comment<caret>");
    CommentByLineCommentAction action = new CommentByLineCommentAction();

    action.actionPerformedImpl(getProject(), myFixture.getEditor());
    myFixture.checkResult("(*comment*)");

    action.actionPerformedImpl(getProject(), myFixture.getEditor());
    myFixture.checkResult("comment");
  }

  public void testLineUncommenter() {
    myFixture.configureByText(OclFileType.INSTANCE, "(* comment *)<caret>");
    CommentByLineCommentAction action = new CommentByLineCommentAction();

    action.actionPerformedImpl(getProject(), myFixture.getEditor());
    myFixture.checkResult(" comment ");
  }

  /*--
  length: 17 / commented: 0-17 / deleteStart: 0-3 / deleteEnd: 14-17
  !(*
    x (* y *)   =>   x (* y *)
  *)!
  <caret>
  --*/
  public void testGH_27() {
    myFixture.configureByText(
        OclFileType.INSTANCE, "<selection>(*\n  x (* y *)\n*)</selection>\n<caret>");

    CommentByBlockCommentAction action = new CommentByBlockCommentAction();
    action.actionPerformedImpl(getProject(), myFixture.getEditor());

    myFixture.checkResult("  x (* y *)\n");
  }

  /*--
  length: 18 / commented: null
                         (*
  ...(* x *)             (* x *)
  y               =>     y
  (* z *)...             (* z *)
                         *)
  --*/
  public void testGH_27b() {
    myFixture.configureByText(
        OclFileType.INSTANCE, "<selection>(* x *)\ny\n(* z *)\n</selection><caret>");

    CommentByBlockCommentAction commentAction = new CommentByBlockCommentAction();
    commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());

    myFixture.checkResult("(*\n(* x *)\ny\n(* z *)\n*)\n");
  }

  public void testGH_27c() {
    myFixture.configureByText(
        OclFileType.INSTANCE, "<selection>(* x *)\n(* z *)\n</selection><caret>");

    CommentByBlockCommentAction commentAction = new CommentByBlockCommentAction();
    commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());

    myFixture.checkResult("(*\n(* x *)\n(* z *)\n*)\n");
  }
}
