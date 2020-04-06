package com.reason.esy;

import com.intellij.openapi.project.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class EsyProcessTest {

  @Mock
  private Project mockProject;

  private EsyProcess instance;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    this.instance = EsyProcess.getInstance(mockProject);
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