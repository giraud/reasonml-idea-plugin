package com.reason.ide.comment;

import com.intellij.codeInsight.generation.actions.*;
import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class OclCommenterTest extends ORBasePlatformTestCase {
    @Test
    public void test_line_commenter() {
        configureCode("A.ml", "  comment<caret>");
        CommentByLineCommentAction action = new CommentByLineCommentAction();

        action.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("(*  comment *)");

        action.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("  comment ");
    }

    @Test
    public void test_line_uncommenter() {
        configureCode("A.ml", "  (* comment *)<caret>");
        CommentByLineCommentAction action = new CommentByLineCommentAction();

        action.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("   comment ");
    }

    @Test
    public void test_line_uncommenter_02() {
        configureCode("A.ml", "  (*comment*)<caret>");
        CommentByLineCommentAction action = new CommentByLineCommentAction();

        action.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("  comment");
    }

    @Test
    public void test_line_uncommenter_03() {
        configureCode("A.ml", "\ntest\n(*    comment    *)    <caret>");
        CommentByLineCommentAction action = new CommentByLineCommentAction();

        action.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("\ntest\n    comment        ");
    }

    /*--
    length: 17 / commented: 0-17 / deleteStart: 0-3 / deleteEnd: 14-17
    !(*
      x (* y *)   =>   x (* y *)
    *)!
    <caret>
    --*/
    @Test
    public void test_GH_27() {
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
    public void test_GH_27b() {
        configureCode("A.ml", "<selection>(* x *)\ny\n(* z *)\n</selection><caret>");

        CommentByBlockCommentAction commentAction = new CommentByBlockCommentAction();
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());

        myFixture.checkResult("(*\n(* x *)\ny\n(* z *)\n*)\n");
    }

    @Test
    public void test_GH_27c() {
        configureCode("A.ml", "<selection>(* x *)\n(* z *)\n</selection><caret>");

        CommentByBlockCommentAction commentAction = new CommentByBlockCommentAction();
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());

        myFixture.checkResult("(*\n(* x *)\n(* z *)\n*)\n");
        assertEquals(19, myFixture.getCaretOffset());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/319
    @Test
    public void test_GH_319() {
        configureCode("A.ml", "line with (<caret>");

        CommentByLineCommentAction a = new CommentByLineCommentAction();
        a.actionPerformedImpl(getProject(), myFixture.getEditor());

        myFixture.checkResult("(* line with ( *)");
    }

    @Test
    public void test_GH_319_b() {
        configureCode("A.ml", " line with ( <caret>");

        CommentByLineCommentAction a = new CommentByLineCommentAction();
        a.actionPerformedImpl(getProject(), myFixture.getEditor());

        myFixture.checkResult("(* line with ( *)");
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/411
    @Test
    public void test_GH_411_comment() {
        configureCode("A.ml", "Infile <selection>of</selection><caret> dirpath");

        CommentByBlockCommentAction commentAction = new CommentByBlockCommentAction();
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());

        myFixture.checkResult("Infile (* of *) dirpath");
        assertEquals(11, myFixture.getCaretOffset());
    }

    @Test
    public void test_GH_411_uncomment() {
        configureCode("A.ml", "Infile <selection>(* of *)</selection><caret> dirpath");

        CommentByBlockCommentAction commentAction = new CommentByBlockCommentAction();
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());

        myFixture.checkResult("Infile of dirpath");
        assertEquals(9, myFixture.getCaretOffset());
    }
}
