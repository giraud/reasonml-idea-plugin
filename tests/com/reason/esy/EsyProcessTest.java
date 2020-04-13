package com.reason.esy;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Before;
import org.junit.Test;

public class EsyProcessTest extends BasePlatformTestCase {

  private EsyProcess instance;

  @Before
  public void setUp() throws Exception {
    this.instance = EsyProcess.getInstance(getProject());
  }

  @Test
  public void start() {
    assertFalse(instance.isStarted());
    instance.start();
    assertTrue(instance.isStarted());
  }

  @Test
  public void terminated() {
    instance.start();
    assertTrue(instance.isStarted());
    instance.terminate();
    assertFalse(instance.isStarted());
  }
}