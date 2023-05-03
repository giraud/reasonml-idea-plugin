package com.reason.ide.comment;

import com.intellij.codeInsight.generation.*;
import com.intellij.lang.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.util.text.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class OclCommenter implements SelfManagingCommenter<OclCommenter.CommenterData>, Commenter, CommenterWithLineSuffix, CustomUncommenter {
    @Override
    public @NotNull String getLineCommentPrefix() {
        return "(*";
    }

    @Override
    public @NotNull String getLineCommentSuffix() {
        return "*)";
    }

    @Override
    public @NotNull String getBlockCommentPrefix() {
        return "(*";
    }

    @Override
    public @NotNull String getBlockCommentSuffix() {
        return "*)";
    }

    @Override
    public @NotNull String getCommentedBlockCommentPrefix() {
        return "(*";
    }

    @Override
    public @NotNull String getCommentedBlockCommentSuffix() {
        return "*)";
    }

    @Override
    public @NotNull CommenterData createLineCommentingState(int startLine, int endLine, @NotNull Document document, @NotNull PsiFile file) {
        return new CommenterData();
    }

    @Override
    public @NotNull CommenterData createBlockCommentingState(int selectionStart, int selectionEnd, @NotNull Document document, @NotNull PsiFile file) {
        return new CommenterData();
    }

    @Override
    public void commentLine(int line, int offset, @NotNull Document document, @NotNull CommenterData data) {
        int lineEndOffset = document.getLineEndOffset(line);

        CharSequence chars = document.getCharsSequence();
        boolean startWithSpace = chars.charAt(offset) == ' ';
        boolean endsWithSpace = chars.charAt(lineEndOffset - 1) == ' ';

        SelfManagingCommenterUtil.insertBlockComment(offset, lineEndOffset, document, startWithSpace ? "(*" : "(* ", endsWithSpace ? "*)" : " *)");
    }

    @Override
    public void uncommentLine(int line, int offset, @NotNull Document document, @NotNull CommenterData data) {
        CharSequence chars = document.getCharsSequence();

        int lineEndOffset = document.getLineEndOffset(line);
        int textEndOffset = CharArrayUtil.shiftBackward(chars, lineEndOffset - 1, " \t");

        SelfManagingCommenterUtil.uncommentBlockComment(offset, textEndOffset + 1, document, "(*", "*)");
    }

    @Override
    public boolean isLineCommented(int line, int offset, @NotNull Document document, @NotNull CommenterData data) {
        CharSequence charSequence = document.getCharsSequence().subSequence(offset, offset + 2);
        return charSequence.toString().equals(getBlockCommentPrefix());
    }

    @Override
    public @NotNull String getCommentPrefix(int line, @NotNull Document document, @NotNull CommenterData data) {
        return "(*";
    }

    @Override
    public @Nullable TextRange getBlockCommentRange(int selectionStart, int selectionEnd, @NotNull Document document, @NotNull CommenterData data) {
        return SelfManagingCommenterUtil.getBlockCommentRange(selectionStart, selectionEnd, document, "(*", "*)");
    }

    @Override
    public @NotNull String getBlockCommentPrefix(int selectionStart, @NotNull Document document, @NotNull CommenterData data) {
        return "(*";
    }

    @Override
    public @NotNull String getBlockCommentSuffix(int selectionEnd, @NotNull Document document, @NotNull CommenterData data) {
        return "*)";
    }

    @Override
    public void uncommentBlockComment(int startOffset, int endOffset, Document document, CommenterData data) {
        CharSequence chars = document.getCharsSequence();

        boolean startHasLF = chars.charAt(startOffset + 2) == '\n';
        boolean endHasLF = chars.charAt(endOffset - 1 - 2) == '\n';

        SelfManagingCommenterUtil.uncommentBlockComment(startOffset, endOffset, document, startHasLF ? "(*\n" : "(*", endHasLF ? "\n*)" : "*)");
    }

    @Override
    public @Nullable TextRange insertBlockComment(int startOffset, int endOffset, Document document, CommenterData data) {
        CharSequence chars = document.getCharsSequence();

        boolean startHasLF = chars.charAt(startOffset) == '\n';
        boolean endHasLF = chars.charAt(endOffset - 1) == '\n';

        return SelfManagingCommenterUtil.insertBlockComment(startOffset, endOffset, document, startHasLF ? "(*" : "(*\n", endHasLF ? "*)\n" : "*)");
    }

    @Override
    public @Nullable TextRange findMaximumCommentedRange(@NotNull CharSequence text) {
        TextRange commentedRange = null;

        // trim start & end
        int selectionStart = 0;
        selectionStart = CharArrayUtil.shiftForward(text, selectionStart, " \t\n");

        int selectionEnd = text.length() - 1;
        selectionEnd = CharArrayUtil.shiftBackward(text, selectionEnd, " \t\n") + 1;

        // Find how many distinct comments in text
        boolean commentStart = CharArrayUtil.regionMatches(text, selectionStart, "(*");
        if (commentStart) {
            int commentCount = 0;
            int nestedComment = 0;
            for (int i = selectionStart; i < selectionEnd; i++) {
                char c = text.charAt(i);
                if (c == '(') {
                    char c2 = text.charAt(i + 1);
                    if (c2 == '*') {
                        nestedComment++;
                    }
                } else if (c == '*') {
                    char c2 = text.charAt(i + 1);
                    if (c2 == ')') {
                        nestedComment--;
                        if (nestedComment == 0) {
                            commentCount++;
                        }
                    }
                }
            }

            if (commentCount == 1
                    && selectionEnd - selectionStart >= 2 + 2
                    && CharArrayUtil.regionMatches(text, selectionEnd - 2, "*)")) {
                commentedRange = new TextRange(selectionStart, selectionEnd);
            }
        }

        return commentedRange;
    }

    @Override
    public @NotNull Collection<? extends Couple<TextRange>> getCommentRangesToDelete(@NotNull CharSequence text) {
        Collection<Couple<TextRange>> ranges = new ArrayList<>();

        // should use nearest after all pairs (* *)

        int start = getNearest((String) text);
        TextRange prefixRange = expandRange(text, start, start + 2);

        int end = ((String) text).lastIndexOf("*)");
        TextRange suffixRange = expandRange(text, end, end + 2);

        ranges.add(Couple.of(prefixRange, suffixRange));
        return ranges;
    }

    private @NotNull TextRange expandRange(@NotNull CharSequence chars, int delOffset1, int delOffset2) {
        int offset1 = CharArrayUtil.shiftBackward(chars, delOffset1 - 1, " \t");
        if (offset1 < 0 || chars.charAt(offset1) == '\n' || chars.charAt(offset1) == '\r') {
            int offset2 = CharArrayUtil.shiftForward(chars, delOffset2, " \t");
            if (offset2 == chars.length()
                    || chars.charAt(offset2) == '\r'
                    || chars.charAt(offset2) == '\n') {
                delOffset1 = (offset1 < 0) ? offset1 + 1 : offset1;
                if (offset2 < chars.length()) {
                    delOffset2 = offset2 + 1;
                    if (chars.charAt(offset2) == '\r'
                            && offset2 + 1 < chars.length()
                            && chars.charAt(offset2 + 1) == '\n') {
                        delOffset2++;
                    }
                }
            }
        }
        return new TextRange(delOffset1, delOffset2);
    }

    private static int getNearest(@NotNull String text) {
        int result = text.indexOf("(*");
        return result == -1 ? text.length() : result;
    }

    static class CommenterData extends CommenterDataHolder {

    }
}
