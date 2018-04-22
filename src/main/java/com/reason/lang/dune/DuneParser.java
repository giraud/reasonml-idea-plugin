package com.reason.lang.dune;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserState;

import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;

public class DuneParser extends CommonParser {
    public DuneParser() {
        super(null);
    }

    @Override
    protected void parseFile(PsiBuilder builder, ParserState state) {
        IElementType tokenType = null;

        //long parseStart = System.currentTimeMillis();

        int c = current_position_(builder);
        while (true) {
            //long parseTime = System.currentTimeMillis();
            //if (5 < parseTime - parseStart) {
            // Protection: abort the parsing if too much time spent
            //break;
            //}

            state.previousTokenType = tokenType;
            tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            if (tokenType == DuneTypes.VERSION) {
                wrapWith(DuneTypes.VERSION, builder);
            }

            if (!empty_element_parsed_guard_(builder, "duneFile", c)) {
                break;
            }

            c = builder.rawTokenIndex();
        }
    }
}
