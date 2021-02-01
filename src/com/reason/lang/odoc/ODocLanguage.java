package com.reason.lang.odoc;

import com.intellij.lang.Language;

public class ODocLanguage extends Language {
  public static final ODocLanguage INSTANCE = new ODocLanguage();

  private ODocLanguage() {
    super("ODoc");
  }
}
