package com.reason.comp.rescript;

import com.reason.comp.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

// See tests for error messages patterns
public class RescriptOutputAnalyzer extends ORCompilerOutputAnalyzer {
    private static final Log LOG = Log.create("rescript.output");

    enum OutputState {
        unknown,
        //
        fileLocation,
        errorMessage,
        //
        syntaxError,
        syntaxErrorLocation,
        syntaxErrorSourceCode,
        syntaxErrorMessage
    }

    private @NotNull OutputState myCurrentState = OutputState.unknown;

    @Override
    public void onTextAvailable(@NotNull String line) {
        if (line.startsWith("File") && (myCurrentState == OutputState.unknown || myCurrentState == OutputState.errorMessage)) {
            myCurrentInfo = extractExtendedFilePositions(LOG, line);
            myCurrentState = OutputState.fileLocation;
        } else if (line.startsWith("Error") && myCurrentState == OutputState.fileLocation) {
            if (myCurrentInfo != null) {
                myCurrentInfo.isError = line.startsWith("Error:"); // else Error (warning xx):
                int pos = line.indexOf(':');
                myCurrentInfo.message = pos > 0 ? line.substring(pos + 1).trim() : "";
            }
            myCurrentState = OutputState.errorMessage;
        } else if (line.startsWith("  Syntax error") && myCurrentState == OutputState.unknown) {
            myCurrentState = OutputState.syntaxError;
        } else if (myCurrentState == OutputState.syntaxError) {
            myCurrentInfo = extractSyntaxErrorFilePosition(LOG, line);
            if (myCurrentInfo != null) {
                myCurrentInfo.isError = true;
            }
            myCurrentState = OutputState.syntaxErrorLocation;
        } else if (line.startsWith("FAILED:")) {
            myCurrentState = OutputState.unknown;
            myCurrentInfo = null;
        } else if (line.startsWith(" ") && myCurrentState == OutputState.errorMessage) {
            if (myCurrentInfo != null) {
                myCurrentInfo.message += " " + line.trim();
            }
        } else if (line.startsWith(" ") && myCurrentState == OutputState.syntaxErrorMessage) {
            if (myCurrentInfo != null) {
                myCurrentInfo.message += (myCurrentInfo.message.isEmpty() ? "" : " ") + line.trim();
            }
        } else if (line.isEmpty() && myCurrentState == OutputState.syntaxErrorLocation) {
            myCurrentState = OutputState.syntaxErrorSourceCode;
        } else if (line.isEmpty() && myCurrentState == OutputState.syntaxErrorSourceCode) {
            myCurrentState = OutputState.syntaxErrorMessage;
        }

    }

}
