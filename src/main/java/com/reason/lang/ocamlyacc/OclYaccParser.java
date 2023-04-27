package com.reason.lang.ocamlyacc;

import com.intellij.lang.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

public class OclYaccParser extends CommonPsiParser {
    protected OclYaccParser() {
        super(true);
    }

    @Override protected ORParser<OclYaccTypes> getORParser(@NotNull PsiBuilder builder) {
        return new OclYaccParserState(builder, !myIsSafe);
    }

    static class OclYaccParserState extends ORParser<OclYaccTypes> {
        protected OclYaccParserState(@NotNull PsiBuilder builder, boolean verbose) {
            super(OclYaccTypes.INSTANCE, builder, verbose);
        }

        @Override
        public void parse() {
            while (!myBuilder.eof()) {
                myBuilder.advanceLexer();
            }
        }
    }
}
