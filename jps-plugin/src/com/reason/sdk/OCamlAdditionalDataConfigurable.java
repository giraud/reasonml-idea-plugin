package com.reason.sdk;

import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.ObjDoubleConsumer;

public class OCamlAdditionalDataConfigurable implements AdditionalDataConfigurable {

  private @Nullable Sdk m_sdk = null;
  private final OCamlSdkForm m_form = new OCamlSdkForm();

  @Override
  public void setSdk(@NotNull Sdk sdk) {
    m_sdk = sdk;
    SdkAdditionalData data = sdk.getSdkAdditionalData();
    if (data == null) {
      String versionString = sdk.getVersionString();
      if (versionString != null) {
        OCamlSdkAdditionalData oData = new OCamlSdkAdditionalData();
        oData.setVersionFromHome(versionString);
        ((ProjectJdkImpl) sdk).setSdkAdditionalData(oData);
      }
    }
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    if (m_sdk != null) {
      m_form.createUIComponents(m_sdk);
      return m_form.getComponent();
    }
    return null;
  }

  @Override
  public boolean isModified() {
    return m_form.isModified();
  }

  @Override
  public void apply() {
    m_form.apply();
  }
}
