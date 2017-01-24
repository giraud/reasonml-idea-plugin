package com.reason.psi.impl;

public class ReasonMLPsiImplUtil {
//    public static String getKey(ReasonMLProperty element) {
//        ASTNode keyNode = element.getNode().findChildByType(ReasonMLTypes.KEY);
//        if (keyNode != null) {
//            // IMPORTANT: Convert embedded escaped spaces to simple spaces
//            return keyNode.getText().replaceAll("\\\\ ", " ");
//        } else {
//            return null;
//        }
//    }
//
//    public static String getValue(ReasonMLProperty element) {
//        ASTNode valueNode = element.getNode().findChildByType(ReasonMLTypes.VALUE);
//        if (valueNode != null) {
//            return valueNode.getText();
//        } else {
//            return null;
//        }
//    }
//
//    public static String getName(ReasonMLProperty element) {
//        return getKey(element);
//    }
//
//    public static PsiElement setName(ReasonMLProperty element, String newName) {
//        ASTNode keyNode = element.getNode().findChildByType(ReasonMLTypes.KEY);
//        if (keyNode != null) {
//            ReasonMLProperty property = ReasonMLElementFactory.createProperty(element.getProject(), newName);
//            ASTNode newKeyNode = property.getFirstChild().getNode();
//            element.getNode().replaceChild(keyNode, newKeyNode);
//        }
//        return element;
//    }
//
//    public static PsiElement getNameIdentifier(ReasonMLProperty element) {
//        ASTNode keyNode = element.getNode().findChildByType(ReasonMLTypes.KEY);
//        if (keyNode != null) {
//            return keyNode.getPsi();
//        } else {
//            return null;
//        }
//    }
//
//    public static ItemPresentation getPresentation(final ReasonMLProperty element) {
//        return new ItemPresentation() {
//            @Nullable
//            @Override
//            public String getPresentableText() {
//                return element.getKey();
//            }
//
//            @Nullable
//            @Override
//            public String getLocationString() {
//                PsiFile containingFile = element.getContainingFile();
//                return containingFile == null ? null : containingFile.getName();
//            }
//
//            @Nullable
//            @Override
//            public Icon getIcon(boolean unused) {
//                return ReasonMLIcons.FILE;
//            }
//        };
//    }
}
