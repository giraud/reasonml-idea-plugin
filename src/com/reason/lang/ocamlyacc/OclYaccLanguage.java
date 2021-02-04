package com.reason.lang.ocamlyacc;

import com.intellij.lang.Language;

public class OclYaccLanguage extends Language {
  public static final OclYaccLanguage INSTANCE = new OclYaccLanguage();

  private OclYaccLanguage() {
    super("Mly");
  }
}
