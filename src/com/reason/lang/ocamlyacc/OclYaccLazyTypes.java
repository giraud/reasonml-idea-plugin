package com.reason.lang.ocamlyacc;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

public interface OclYaccLazyTypes {
    ILazyParseableElementType OCAML_LAZY_NODE = new ILazyParseableElementType("OCAML_LAZY_NODE", OclLanguage.INSTANCE) {
        @Override
        public ASTNode parseContents(@NotNull ASTNode chameleon) {
            return OclParser.parseOcamlNode(this, chameleon);
        }
    };
}
