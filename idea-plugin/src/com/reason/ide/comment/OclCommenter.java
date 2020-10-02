package com.reason.ide.comment;

import com.intellij.lang.Commenter;
import com.intellij.lang.CustomUncommenter;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.text.CharArrayUtil;
import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OclCommenter implements Commenter, CustomUncommenter {
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
  @Override
  public TextRange findMaximumCommentedRange(@NotNull CharSequence text) {
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

  @NotNull
  @Override
  public Collection<? extends Couple<TextRange>> getCommentRangesToDelete(
      @NotNull CharSequence text) {
    Collection<Couple<TextRange>> ranges = new ArrayList<>();

    // should use nearest after all pairs (* *)

    int start = getNearest((String) text);
    TextRange prefixRange = expandRange(text, start, start + 2);

    int end = ((String) text).lastIndexOf("*)");
    TextRange suffixRange = expandRange(text, end, end + 2);

    ranges.add(Couple.of(prefixRange, suffixRange));
    return ranges;
  }

  @NotNull
  private TextRange expandRange(@NotNull CharSequence chars, int delOffset1, int delOffset2) {
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
}
