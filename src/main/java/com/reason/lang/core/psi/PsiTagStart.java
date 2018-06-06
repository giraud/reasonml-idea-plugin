package com.reason.lang.core.psi;

import java.util.Map;

public interface PsiTagStart extends PsiNamedElement {
    Map<String, String> getAttributes(); // Map is not a good API
}
