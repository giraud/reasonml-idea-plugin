package com.reason.comp.dune;

import com.reason.comp.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

// See tests for error messages patterns
// Need some version aware analyzer ? see https://github.com/giraud/reasonml-idea-plugin/issues/174
public class DuneOutputAnalyzer extends ORCompilerOutputAnalyzer {
    private static final Log LOG = Log.create("dune.output");

    /*
     unknown -> fileLocation

     fileLocation -> message
                  -> sourceCode

     sourceCode -> message
     */
    enum OutputState {
        unknown,
        fileLocation,
        sourceCode,
        message,
    }

    private @NotNull OutputState myState = OutputState.unknown;

    @Override
    public void onTextAvailable(@NotNull String line) {
        // State transition: unknown -> fileLocation
        if (line.startsWith("File") && myState == OutputState.unknown) {
            myCurrentInfo = extractExtendedFilePositions(LOG, line);
            myState = OutputState.fileLocation;
        }
        // State transition: fileLocation|sourceCode -> message [ERROR]
        else if (line.startsWith("Error:") && (myState == OutputState.fileLocation || myState == OutputState.sourceCode)) {
            myState = OutputState.message;
            if (myCurrentInfo != null) {
                myCurrentInfo.isError = true;
                myCurrentInfo.message = line.substring(6).trim();
            }
        }
        // Error message might be on multiple lines
        else if (myState == OutputState.message && myCurrentInfo != null && myCurrentInfo.isError && line.startsWith(" ")) {
            boolean endMessage = true;

            String trimmedLine = line.trim();
            if (!trimmedLine.isEmpty() && !trimmedLine.startsWith("File")) {
                myCurrentInfo.message += " " + trimmedLine;
                endMessage = trimmedLine.endsWith(".");
            }

            if (endMessage) {
                myState = OutputState.unknown;
                myCurrentInfo = null;
            }
        }
        // State transition: fileLocation|sourceCode -> message [WARNING]
        else if (line.startsWith("Warning") && (myState == OutputState.fileLocation || myState == OutputState.sourceCode)) {
            myState = OutputState.message;
            if (myCurrentInfo != null) {
                myCurrentInfo.isError = false;
                int pos = line.indexOf(":");
                myCurrentInfo.message = line.substring(pos + 1).trim();
            }
        }
        // State transition: fileLocation -> sourceCode
        else if (myState == OutputState.fileLocation) {
            myState = OutputState.sourceCode;
        }
        // Fallback
        else if (myState != OutputState.sourceCode && myState != OutputState.unknown) {
            myState = OutputState.unknown;
            myCurrentInfo = null;
        }
    }
}
