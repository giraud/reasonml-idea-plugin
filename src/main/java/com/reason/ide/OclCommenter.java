package com.reason.ide;

import com.intellij.lang.Commenter;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.text.CharArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class OclCommenter implements Commenter/*, CustomUncommenter*/ {
    @Nullable
    @Override
    public String getLineCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getBlockCommentPrefix() {
        return "(*";
    }

    @Nullable
    @Override
    public String getBlockCommentSuffix() {
        return "*)";
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentPrefix() {
        return "(*";
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentSuffix() {
        return "*)";
    }

    @Nullable
    //@Override
    public TextRange findMaximumCommentedRange(@NotNull CharSequence text) {
        // copied and adapted com.intellij.codeInsight.generation.CommentByBlockCommentHandler.getSelectedComments
        TextRange commentedRange = null;

        String prefix = getBlockCommentPrefix();
        String suffix = getBlockCommentSuffix();

        int selectionStart = 0;
        selectionStart = CharArrayUtil.shiftForward(text, selectionStart, " \t\n");
        int selectionEnd = text.length() - 1;
        selectionEnd = CharArrayUtil.shiftBackward(text, selectionEnd, " \t\n") + 1;
        boolean b = CharArrayUtil.regionMatches(text, selectionEnd - suffix.length(), suffix);
        if (selectionEnd - selectionStart >= prefix.length() + suffix.length() &&
                CharArrayUtil.regionMatches(text, selectionStart, prefix) &&
                CharArrayUtil.regionMatches(text, selectionEnd - suffix.length(), suffix)) {
            commentedRange = new TextRange(selectionStart, selectionEnd);
        }

        return commentedRange;

    }

    @NotNull
    //@Override
    public Collection<? extends Couple<TextRange>> getCommentRangesToDelete(@NotNull CharSequence text) {
        Collection<Couple<TextRange>> ranges = new ArrayList<>();

        int start = getNearest((String) text, "(*", 0);
        TextRange prefixRange = expandRange(text, start, start + 2);

        // should use nearest after all pairs (* *)
        int end = ((String) text).lastIndexOf("*)");
        TextRange suffixRange = expandRange(text, end, end + 2);

        ranges.add(Couple.of(prefixRange, suffixRange));
        return ranges;
    }

    private TextRange expandRange(CharSequence chars, int delOffset1, int delOffset2) {
        int offset1 = CharArrayUtil.shiftBackward(chars, delOffset1 - 1, " \t");
        if (offset1 < 0 || chars.charAt(offset1) == '\n' || chars.charAt(offset1) == '\r') {
            int offset2 = CharArrayUtil.shiftForward(chars, delOffset2, " \t");
            if (offset2 == chars.length() || chars.charAt(offset2) == '\r' || chars.charAt(offset2) == '\n') {
                delOffset1 = offset1 + 1;
                if (offset2 < chars.length()) {
                    delOffset2 = offset2 + 1;
                    if (chars.charAt(offset2) == '\r' && offset2 + 1 < chars.length() && chars.charAt(offset2 + 1) == '\n') {
                        delOffset2++;
                    }
                }
            }
        }
        return new TextRange(delOffset1, delOffset2);
    }

    private static int getNearest(String text, String pattern, int position) {
        int result = text.indexOf(pattern, position);
        return result == -1 ? text.length() : result;
    }
}