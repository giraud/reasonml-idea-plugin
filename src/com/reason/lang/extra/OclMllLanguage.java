package com.reason.lang.extra;

import com.intellij.lang.Language;

public class OclMllLanguage extends Language {
  public static final OclMllLanguage INSTANCE = new OclMllLanguage();

  private OclMllLanguage() {
    super("Mll");
  }
}
