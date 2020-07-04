package com.reason.lang.napkin;

public class ClassParsingTest extends NsParsingTestCase {

    public void testBasic() {
        //    Collection<PsiClass> classes = classExpressions(parseCode("class foo = {  as _; }"));
        //
        //    assertEquals(1, classes.size());
        //    assertEquals("foo", first(classes).getName());
    }

    //public void testClassType() {
    //    Collection<PsiClass> classes = classExpressions(parseCode("class type restricted_point_type = { pub get_x: int; pub bump: unit; }"));
    //
    //    assertEquals(1, classes.size());
    //    assertEquals("restricted_point_type", first(classes).getName());
    //}

    //public void testFields() {
    //    Collection<PsiClass> classes = classExpressions(parseCode("class foo = { as _; val mutable a = []; val b = 2; }"));
    //
    //    PsiClass clazz = first(classes);
    //    Collection<PsiClassField> fields = clazz.getFields();
    //    assertEquals(fields.size(), 2);
    //}

    //public void testMethods() {
    //    Collection<PsiClass> classes = classExpressions(parseCode("class foo = { as _; pub get_x = x; pub get_y = y; }"));
    //
    //    PsiClass clazz = first(classes);
    //    Collection<PsiClassMethod> methods = clazz.getMethods();
    //    assertEquals(methods.size(), 2);
    //}
    //
    //public void testBoth() {
    //    Collection<PsiClass> classes = classExpressions(parseCode("class foo = { as _; val mutable x = []; pub get_x = x; }"));
    //
    //    PsiClass clazz = first(classes);
    //    assertEquals(clazz.getFields().size(), 1);
    //    assertEquals(clazz.getMethods().size(), 1);
    //}
    //
    //public void testClassConstruct() {
    //    Collection<PsiClass> classes = classExpressions(parseCode("class c (m: int) = { as self;pub m = m;initializer (all_c := [(self :> c), ...all_c^]); }"));
    //
    //    PsiClass clazz = first(classes);
    //    assertEquals(clazz.getParameters().size(), 0);
    //    assertNotNull(clazz.getConstructor());
    //}
    //
    //public void testClassConstraint() {
    //    Collection<PsiClass> classes = classExpressions(parseCode(
    //            "class circle ('a) (c: 'a) = { as _; constraint 'a = #point; val mutable center = c; pub set_center = c => center = c; pub move = center#move; }"));
    //
    //    PsiClass clazz = first(classes);
    //    assertEquals("circle", first(classes).getName());
    //    assertNotNull(clazz.getParameters());
    //    assertNotNull(clazz.getConstructor());
    //    assertEquals(clazz.getFields().size(), 1);
    //    assertEquals(clazz.getMethods().size(), 2);
    //}
}
