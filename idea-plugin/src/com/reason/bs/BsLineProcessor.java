package com.reason.bs;

import com.reason.Log;
import com.reason.ide.annotations.OutputInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.reason.bs.BsLineProcessor.BuildStatus.*;
import static java.lang.Integer.parseInt;

/** Line processor is a state machine. */
public class BsLineProcessor {

  private static final Pattern FILE_LOCATION =
      Pattern.compile("File \"(.+)\", line (\\d+), characters (\\d+)-(\\d+):\n");

  private static final Pattern POSITIONS = Pattern.compile("[\\s:]\\d+:\\d+(-\\d+(:\\d+)?)?$");

  private final Log m_log;

  public BsLineProcessor(Log log) {
    m_log = log;
  }

  enum BuildStatus {
    unknown, // warning steps
    warningDetected,
    warningLinePos,
    warningSourceExtract,
    warningMessage, // error steps
    errorDetected,
    errorLinePos,
    errorSourceExtract,
    errorMessage, // syntax error
    syntaxError
  }

  @NotNull private BuildStatus m_status = BuildStatus.unknown;

  @Nullable private OutputInfo m_latestInfo = new OutputInfo();
  @NotNull private String m_previousText = "";

  private final List<OutputInfo> m_bsbInfo = new ArrayList<>();

  public @NotNull List<OutputInfo> getInfo() {
    return m_bsbInfo;
  }

  public void onRawTextAvailable(@NotNull String text) {
    String trimmedText = text.trim();

    switch (m_status) {
        /*
        Warning
        */
      case warningDetected:
        // Must contain warning location (file/position)
        // ...path\src\File.re 61:10
        m_latestInfo = extractFilePositions(text);
        if (m_latestInfo != null) {
          m_latestInfo.isError = false;
        }
        m_status = warningLinePos;
        break;
      case warningLinePos:
      case errorLinePos:
        if (m_latestInfo != null && !trimmedText.isEmpty()) {
          m_status = m_latestInfo.isError ? errorSourceExtract : warningSourceExtract;
        }
        break;
      case warningSourceExtract:
      case errorSourceExtract:
        trimmedText = text.trim();
        if (m_latestInfo != null && trimmedText.isEmpty()) {
          m_status = m_latestInfo.isError ? errorMessage : warningMessage;
        }
        break;
      case warningMessage:
      case errorMessage:
        if (trimmedText.isEmpty() || text.charAt(0) != ' ') {
          // create bsb info
          if (m_latestInfo != null) {
            m_latestInfo.message = m_latestInfo.message.trim();
          }
          m_status = unknown;
        } else if (m_latestInfo != null) {
          m_latestInfo.message += text;
        }
        break;
        /*
        Error
        */
      case errorDetected:
        // Must contain error location (file/position)
        // ...path\src\File.re 61:10-23
        m_latestInfo = extractFilePositions(text);
        if (m_latestInfo != null) {
          m_latestInfo.isError = true;
        }
        m_status = errorLinePos;
        break;
      default:
        if (trimmedText.startsWith("Warning number")) {
          reset();
          m_status = warningDetected;
        } else if (text.startsWith("Error:")) {
          // It's a one line message
          m_status = syntaxError;
          if (m_previousText.startsWith("File")) {
            m_latestInfo = extractExtendedFilePositions(m_previousText);
            if (m_latestInfo != null) {
              m_latestInfo.message = text.substring(6).trim();
            }
          }
        } else if (trimmedText.startsWith("We've found a bug for you")) {
          if (m_status != syntaxError) {
            reset();
            m_status = errorDetected;
          }
        } else if (m_latestInfo != null && trimmedText.startsWith("Hint:")) {
          m_latestInfo.message += ". " + text.trim();
        }
    }

    m_previousText = text;
  }

  public void reset() {
    m_status = unknown;
    m_latestInfo = null;
    m_previousText = "";
  }

  // File "...path/src/Source.re", line 111, characters 0-3:
  @Nullable
  private OutputInfo extractExtendedFilePositions(@Nullable String text) {
    if (text != null) {
      Matcher matcher = FILE_LOCATION.matcher(text);
      if (matcher.matches()) {
        String path = matcher.group(1);
        String line = matcher.group(2);
        String colStart = matcher.group(3);
        String colEnd = matcher.group(4);
        OutputInfo info = addInfo(path, line, colStart, colEnd);
        if (info.colStart < 0 || info.colEnd < 0) {
          m_log.error("Can't decode columns for [" + text + "]");
          return null;
        }
        return info;
      }
    }

    return null;
  }

  // "...path/src/Source.re 111:21-112:22" or " ...path/src/Source.re:111:21-112:22"
  // "...path/src/Source.re 111:21-22" or "...path/src/Source.re:111:21-22"
  // "...path/src/Source.re 111:21" or "...path/src/Source.re:111:21"
  @Nullable
  private OutputInfo extractFilePositions(@Nullable String text) {
    if (text == null) {
      return null;
    }
    String trimmed = text.trim();
    Matcher matcher = POSITIONS.matcher(trimmed);
    // extract path and positions
    if (matcher.find()) {
      String positions = matcher.group();
      // remove positions from text to get path
      String path = text.replace(positions, "");
      // remove leading space or colon from positions
      positions = positions.substring(1);

      // split "111:21-112:22" into ["111:21", "112:22"]
      String[] startAndEndPositions = positions.split("-");

      // only start positions found, ["111:21"]
      String[] startLineAndCol = startAndEndPositions[0].split(":");
      if (startAndEndPositions.length == 1) {
        return addInfo(path, startLineAndCol[0], startLineAndCol[1], null, null);
      }

      // both start and end positions present
      if (startAndEndPositions.length == 2) {
        String[] endLineAndCol = startAndEndPositions[1].split(":");
        // "111:21-22" --> "111:21-111:22"
        if (endLineAndCol.length == 1) {
          return addInfo(
              path, startLineAndCol[0], startLineAndCol[1], startLineAndCol[0], endLineAndCol[0]);
        }
        // "111:21-112:22"
        if (endLineAndCol.length == 2) {
          return addInfo(
              path, startLineAndCol[0], startLineAndCol[1], endLineAndCol[0], endLineAndCol[1]);
        }
      }
      m_log.error("Can't decode columns for [" + text + "]");
    }
    return null;
  }

  @NotNull
  private OutputInfo addInfo(
      @NotNull String path,
      @NotNull String lineStart,
      @NotNull String colStart,
      @Nullable String lineEnd,
      @Nullable String colEnd) {
    OutputInfo info = new OutputInfo();
    info.path = path;
    info.lineStart = parseInt(lineStart);
    info.colStart = parseInt(colStart);
    info.lineEnd = lineEnd == null ? info.lineStart : parseInt(lineEnd);
    info.colEnd = colEnd == null ? info.colStart + 1 : parseInt(colEnd) + 1;
    m_bsbInfo.add(info);
    return info;
  }

  @NotNull
  private OutputInfo addInfo(
      @NotNull String path,
      @NotNull String line,
      @NotNull String colStart,
      @NotNull String colEnd) {
    OutputInfo info = new OutputInfo();
    info.path = path;
    info.lineStart = parseInt(line);
    info.colStart = parseInt(colStart);
    info.lineEnd = info.lineStart;
    info.colEnd = parseInt(colEnd);
    if (info.colEnd == info.colStart) {
      info.colEnd += 1;
    }
    m_bsbInfo.add(info);
    return info;
  }
}
