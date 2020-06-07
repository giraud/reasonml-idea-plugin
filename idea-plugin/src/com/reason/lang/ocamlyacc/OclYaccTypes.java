// This is a generated file. Not intended for manual editing.
package com.reason.lang.ocamlyacc;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.ocamlyacc.impl.*;
import org.jetbrains.annotations.NotNull;

public interface OclYaccTypes extends OclYaccLazyTypes {

    IElementType DECLARATION = new OclYaccElementType("DECLARATION");
    IElementType HEADER = new OclYaccElementType("HEADER");
    IElementType RULE = new OclYaccElementType("RULE");
    IElementType RULE_BODY = new OclYaccElementType("RULE_BODY");
    IElementType RULE_PATTERN = new OclYaccElementType("RULE_PATTERN");
    IElementType TRAILER = new OclYaccElementType("TRAILER");

    IElementType COLON = new OclYaccTokenType("COLON");
    IElementType COMMENT = new OclYaccTokenType("COMMENT");
    IElementType DOT = new OclYaccTokenType("DOT");
    IElementType GT = new OclYaccTokenType("GT");
    IElementType HEADER_START = new OclYaccTokenType("HEADER_START");
    IElementType HEADER_STOP = new OclYaccTokenType("HEADER_STOP");
    IElementType IDENT = new OclYaccTokenType("IDENT");
    IElementType LBRACE = new OclYaccTokenType("LBRACE");
    IElementType LEFT = new OclYaccTokenType("LEFT");
    IElementType LT = new OclYaccTokenType("LT");
    IElementType PIPE = new OclYaccTokenType("PIPE");
    IElementType RBRACE = new OclYaccTokenType("RBRACE");
    IElementType RIGHT = new OclYaccTokenType("RIGHT");
    IElementType SECTION_SEPARATOR = new OclYaccTokenType("SECTION_SEPARATOR");
    IElementType SEMI = new OclYaccTokenType("SEMI");
    IElementType START = new OclYaccTokenType("START");
    IElementType TOKEN = new OclYaccTokenType("TOKEN");
    IElementType TYPE = new OclYaccTokenType("TYPE");

    class Factory {
        @NotNull
        public static PsiElement createElement(@NotNull ASTNode node) {
            IElementType type = node.getElementType();
            if (type == DECLARATION) {
                return new OclYaccDeclarationImpl(node);
            } else if (type == HEADER) {
                return new OclYaccHeaderImpl(node);
            } else if (type == RULE) {
                return new OclYaccRuleImpl(node);
            } else if (type == RULE_BODY) {
                return new OclYaccRuleBodyImpl(node);
            } else if (type == RULE_PATTERN) {
                return new OclYaccRulePatternImpl(node);
            } else if (type == TRAILER) {
                return new OclYaccTrailerImpl(node);
            }
            throw new AssertionError("Unknown element type: " + type);
        }
    }
}
