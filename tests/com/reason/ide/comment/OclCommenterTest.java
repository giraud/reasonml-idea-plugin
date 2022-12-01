package com.reason.ide.comment;

import com.intellij.codeInsight.generation.actions.*;
import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class OclCommenterTest extends ORBasePlatformTestCase {
    @Test
    public void testLineCommenter() {
        configureCode("A.ml", "comment<caret>");
        CommentByLineCommentAction action = new CommentByLineCommentAction();

        action.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("(*comment*)");

        action.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("comment");
    }

    @Test
    public void testLineUncommenter() {
        configureCode("A.ml", "(* comment *)<caret>");
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
    @Test
    public void testGH_27() {
        configureCode("A.ml", "<selection>(*\n  x (* y *)\n*)</selection>\n<caret>");

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
    @Test
    public void testGH_27b() {
        configureCode("A.ml", "<selection>(* x *)\ny\n(* z *)\n</selection><caret>");

        CommentByBlockCommentAction commentAction = new CommentByBlockCommentAction();
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());

        myFixture.checkResult("(*\n(* x *)\ny\n(* z *)\n*)\n");
    }

    @Test
    public void testGH_27c() {
        configureCode("A.ml", "<selection>(* x *)\n(* z *)\n</selection><caret>");

        CommentByBlockCommentAction commentAction = new CommentByBlockCommentAction();
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());

        myFixture.checkResult("(*\n(* x *)\n(* z *)\n*)\n");
    }
}
