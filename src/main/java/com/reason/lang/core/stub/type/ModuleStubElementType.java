package com.reason.lang.core.stub.type;

public class ModuleStubElementType /*extends ILightStubElementType<ModuleStub, ModuleImpl>*/ {
    //    public static final IStubElementType INSTANCE = new ModuleStubElementType();
    //
    //    private ModuleStubElementType() {
    //        super("MODULE_EXPRESSION", RmlLanguage.INSTANCE);
    //    }
    //
    //
    //    public ModuleImpl createPsi(@NotNull final ModuleStub stub) {
    //        return null;//new ModuleImpl(stub, this);
    //    }
    //
    //    @NotNull
    //    public ModuleStub createStub(@NotNull final ModuleImpl psi, final StubElement parentStub) {
    //        return new ModuleStubImpl(parentStub, psi.getName());
    //    }
    //
    //    @NotNull
    //    public String getExternalId() {
    //        return "ReasonML.index.module";
    //    }
    //
    //    public void serialize(@NotNull final ModuleStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
    //        dataStream.writeName(stub.getName());
    //    }
    //
    //    @NotNull
    //    public ModuleStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
    //        final StringRef ref = dataStream.readName();
    //        return new ModuleStubImpl(parentStub, ref.getString());
    //    }
    //
    //    public void indexStub(@NotNull final ModuleStub stub, @NotNull final IndexSink sink) {
    //        sink.occurrence(IndexKeys.MODULES, stub.getName());
    //    }
    //
    //    @Override
    //    public ModuleStub createStub(LighterAST tree, LighterASTNode node, StubElement parentStub) {
    //        LighterASTNode keyNode = LightTreeUtil.firstChildOfType(tree, node, RmlTypes.VALUE_NAME);
    //        String key = intern(tree.getCharTable(), keyNode);
    //        return new ModuleStubImpl(parentStub, key);
    //    }
    //
    //    private static String intern(@NotNull CharTable table, @NotNull LighterASTNode node) {
    //        assert node instanceof LighterASTTokenNode : node;
    //        return table.intern(((LighterASTTokenNode) node).getText()).toString();
    //    }
}
