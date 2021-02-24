package com.reason.lang.napkin;

import com.intellij.lang.Language;

public class ResLanguage extends Language {
  public static final ResLanguage INSTANCE = new ResLanguage();

  private ResLanguage() {
    super("NapkinScript");
  }
}
