package com.reason.lang.core.psi.reference;

import com.intellij.testFramework.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class ResolutionTest extends LightPlatformTestCase {
    public void test_path_traversal() {
        Resolution resolution = new Resolution(new String[]{"A", "B", "C"}, crateLowerElement());

        assertEquals("C", resolution.getCurrentName());
        resolution.updateCurrentWeight(1);
        assertEquals("B", resolution.getCurrentName());
        resolution.updateCurrentWeight(1);
        assertEquals("A", resolution.getCurrentName());
        resolution.updateCurrentWeight(1);
        assertNull(resolution.getCurrentName());
    }

    public void test_alternate_path_traversal() {
        Resolution resolution = new Resolution(new String[]{"A", "B", "C"}, crateLowerElement());
        Resolution altResolution = Resolution.createAlternate(resolution, new String[]{"X", "Y"});

        assertEquals("C", altResolution.getCurrentName());
        altResolution.updateCurrentWeight(1);
        assertEquals("B", altResolution.getCurrentName());
        altResolution.updateCurrentWeight(1);
        assertEquals("Y", altResolution.getCurrentName());
        altResolution.updateCurrentWeight(1);
        assertEquals("X", altResolution.getCurrentName());
        altResolution.updateCurrentWeight(1);
        assertTrue(altResolution.myIsComplete);
    }

    public void test_join_path() {
        Resolution resolution = new Resolution(new String[]{"A", "B", "C"}, crateLowerElement());
        Resolution altResolution = Resolution.createAlternate(resolution, new String[]{"X", "Y"});

        assertEquals("A.B.C", resolution.joinPath());
        assertEquals("X.Y.B.C", altResolution.joinPath());
    }

    public void test_module_name() {
        Resolution resolution = new Resolution(new String[]{"A", "B", "C"}, crateLowerElement());
        Resolution altResolution = Resolution.createAlternate(resolution, new String[]{"X", "Y"});

        assertEquals("A", resolution.getTopModuleName());
        assertEquals("X", altResolution.getTopModuleName());
    }

    public void test_path_equality() {
        Resolution resolution = new Resolution(new String[]{"A", "B", "C"}, crateLowerElement());
        Resolution altResolution = Resolution.createAlternate(resolution, new String[]{"X", "Y"});

        assertTrue(resolution.isPathEqualTo(new String[]{"A", "B", "C"}));
        assertTrue(altResolution.isPathEqualTo(new String[]{"X", "Y", "B", "C"}));
    }

    @SuppressWarnings("ConstantConditions")
    private @NotNull PsiType crateLowerElement() {
        return (PsiType) ORCodeFactory.createTypeName(getProject(), "t").getParent();
    }
}
