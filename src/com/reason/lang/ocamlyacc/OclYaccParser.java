// This is a generated file. Not intended for manual editing.
package com.reason.lang.ocamlyacc;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;

public class OclYaccParser extends CommonParser<OclYaccTypes> {
    protected OclYaccParser() {
        super(OclYaccTypes.INSTANCE);
    }

    @Override protected void parseFile(PsiBuilder builder, ParserState parserState) {
        IElementType tokenType;
        int c = current_position_(builder);
        while (true) {
            tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            builder.advanceLexer();

            if (!empty_element_parsed_guard_(builder, "oclYaccFile", c)) {
                break;
            }

            c = builder.rawTokenIndex();
        }
    }
}
