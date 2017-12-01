package com.reason.lang.core.stub.type;

import com.intellij.lang.LighterAST;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.LighterASTTokenNode;
import com.intellij.psi.impl.source.tree.LightTreeUtil;
import com.intellij.psi.stubs.*;
import com.intellij.util.CharTable;
import com.intellij.util.io.StringRef;
import com.reason.lang.RmlLanguage;
import com.reason.lang.RmlTypes;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.stub.ModuleStub;
import com.reason.lang.core.stub.ModuleStubImpl;
import com.reason.lang.core.stub.index.RmlStubIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ModuleStubElementType /*extends ILightStubElementType<ModuleStub, PsiModule>*/ {
//    public static final IStubElementType INSTANCE = new ModuleStubElementType();
//
//    private ModuleStubElementType() {
//        super("MODULE_EXPRESSION", RmlLanguage.INSTANCE);
//    }
//
//
//    public PsiModule createPsi(@NotNull final ModuleStub stub) {
//        return null;//new PsiModule(stub, this);
//    }
//
//    @NotNull
//    public ModuleStub createStub(@NotNull final PsiModule psi, final StubElement parentStub) {
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
//        sink.occurrence(RmlStubIndexKeys.MODULES, stub.getName());
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
