package com.reason.comp.dune;

import com.reason.comp.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

// See tests for error messages patterns
// Need some version aware analyzer ? see https://github.com/giraud/reasonml-idea-plugin/issues/174
public class DuneOutputAnalyzer extends ORCompilerOutputAnalyzer {
    private static final Log LOG = Log.create("dune.output");

    enum OutputState {
        unknown,
        //
        fileLocation,
        sourceCode,
        errorMessage,
    }

    private @NotNull OutputState myCurrentState = OutputState.unknown;

    @Override
    public void onTextAvailable(@NotNull String line) {
        if (line.startsWith("File") && myCurrentState == OutputState.unknown) {
            myCurrentInfo = extractExtendedFilePositions(LOG, line);
            myCurrentState = OutputState.fileLocation;
        } else if (line.startsWith("Error:") && (myCurrentState == OutputState.fileLocation || myCurrentState == OutputState.sourceCode)) {
            if (myCurrentInfo != null) {
                myCurrentInfo.isError = true;
                myCurrentInfo.message = line.substring(6).trim();
            }
            myCurrentState = OutputState.errorMessage;
        } else if (line.startsWith("Hint:") && myCurrentState == OutputState.errorMessage) {
            if (myCurrentInfo != null) {
                myCurrentInfo.message += " (" + line.trim() + ")";
            }
        } /*else if (line.startsWith("   ")) {
            if (myLatestInfo != null) {
                myLatestInfo.message = myLatestInfo.message + " " + line.trim();
            }
        } */ else if (myCurrentState == OutputState.fileLocation) {
            myCurrentState = OutputState.sourceCode;
        } else if (myCurrentState != OutputState.sourceCode && myCurrentState != OutputState.unknown) {
            myCurrentState = OutputState.unknown;
        }
    }
}
