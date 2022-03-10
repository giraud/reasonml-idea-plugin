package jpsplugin.com.reason;

import org.jetbrains.annotations.*;

import java.util.*;

public class StringUtil {
  private StringUtil() {
  }

  @NotNull
  public static String toFirstUpper(@Nullable String value) {
    if (value == null || value.isEmpty()) {
      return "";
    }

    return value.substring(0, 1).toUpperCase(Locale.getDefault()) + value.substring(1);
  }

  @NotNull
  public static String toFirstLower(@Nullable String value) {
    if (value == null || value.isEmpty()) {
      return "";
    }

    return value.substring(0, 1).toLowerCase(Locale.getDefault()) + value.substring(1);
  }

  public static @Nullable String trimLastCR(@Nullable String value) {
    if (value == null || value.isEmpty()) {
      return value;
    }
    int length = value.length();
    if (value.charAt(length - 1) == '\n') {
      return value.substring(0, length - 1);
    }
    return value;
  }
}
