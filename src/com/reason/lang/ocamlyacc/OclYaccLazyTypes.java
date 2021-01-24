package com.reason.lang.ocamlyacc;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.ILazyParseableElementType;
import com.reason.lang.ocaml.OclLanguage;
import com.reason.lang.ocaml.OclParser;
import org.jetbrains.annotations.NotNull;

public interface OclYaccLazyTypes {
  ILazyParseableElementType OCAML_LAZY_NODE =
      new ILazyParseableElementType("OCAML_LAZY_NODE", OclLanguage.INSTANCE) {
        @Override
        public ASTNode parseContents(@NotNull ASTNode chameleon) {
          return OclParser.parseOcamlNode(this, chameleon);
        }
      };
}
