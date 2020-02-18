package com.reason.ide.reference;

import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.reason.RmlQNameFinder;

import java.util.Set;

public class RmlQNameFinderTest extends ORBasePlatformTestCase {

    public void testLetBinding() {
        FileBase f = configureCode("A.re", "let make = { increase<caret>(); }");

        Set<String> paths = new RmlQNameFinder().extractPotentialPaths(myFixture.getElementAtCaret());
        assertSameElements(paths, "A.make", "A");
    }

    public void testLocalOpenList() {
        FileBase f = configureCode("A.re", "let item = Css.[ margin<caret>");

        Set<String> paths = new RmlQNameFinder().extractPotentialPaths(myFixture.getElementAtCaret());
        assertSameElements(paths, "Css.Css"/*?*/, "Css", "A.item.Css", "A.Css");
    }
}
