package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Collections.*;

public class RPsiMethodCall extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiMethodCall(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }
}
