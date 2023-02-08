package com.reason.comp.bs;

import com.intellij.openapi.vfs.*;
import gnu.trove.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class BsConfig {

  private final String m_name;
  private final String m_namespace;
  private final String m_jsxVersion;
  private final String m_rootBsPlatform;
  private final Set<String> m_externals = new THashSet<>();
  private final Set<String> m_sources = new THashSet<>();
  private final Set<String> m_devSources = new THashSet<>();
  private final Set<String> m_deps = new THashSet<>();
  private final Set<Path> m_paths = new THashSet<>();
  private final String[] m_ppx;
  private boolean m_useExternalAsSource = false;
  private Path m_basePath = null;

  BsConfig(@NotNull String name, @Nullable String namespace, @Nullable String jsxVersion, @Nullable Set<String> sources,
           @Nullable Set<String> devSources, @Nullable Set<String> externals, @Nullable Set<String> deps, @Nullable List<String> ppx) {
    m_name = name;
    m_namespace = namespace == null ? "" : namespace;
    m_jsxVersion = jsxVersion;
    m_rootBsPlatform = FileSystems.getDefault().getPath("node_modules", "bs-platform").toString();
    m_ppx = ppx == null ? new String[0] : ppx.toArray(new String[0]);
    if (sources != null) {
      m_sources.addAll(sources);
    }
    if (devSources != null) {
      m_devSources.addAll(devSources);
    }
    if (externals != null) {
      m_externals.addAll(externals);
    }
    if (deps != null) {
      m_deps.addAll(deps);
      m_paths.addAll(
          deps.stream()
              .map(dep -> FileSystems.getDefault().getPath("node_modules", dep, "lib"))
              .collect(Collectors.toSet()));
    }
  }

  public void setRootFile(@Nullable VirtualFile rootFile) {
    m_basePath = rootFile == null ? null : FileSystems.getDefault().getPath(rootFile.getPath());
  }

  @NotNull
  public String getNamespace() {
    return m_namespace;
  }

  public boolean hasNamespace() {
    return !m_namespace.isEmpty();
  }

  boolean accept(@Nullable String canonicalPath) {
    if (canonicalPath == null || m_basePath == null) {
      return false;
    }

    Path relativePath = m_basePath.relativize(new File(canonicalPath).toPath());
    if (relativePath.startsWith("node_modules")) {
      if (relativePath.startsWith(m_rootBsPlatform)) {
        return true;
      }
      for (Path path : m_paths) {
        if (relativePath.startsWith(path)) {
          return true;
        }
      }
      return false;
    }

    return !relativePath.startsWith("..");
  }

  @NotNull
  public Set<String> getSources() {
    return m_useExternalAsSource ? m_externals : m_sources;
  }

  @NotNull
  public Set<String> getDevSources() {
    return m_devSources;
  }

  @NotNull
  public String getName() {
    return m_name;
  }

  public boolean isInSources(@NotNull VirtualFile file) {
    if (m_basePath != null) {
      Path relativePath = m_basePath.relativize(new File(file.getPath()).toPath());
      for (String source : getSources()) {
        if (relativePath.startsWith(source)) {
          return true;
        }
      }
      for (String source : m_devSources) {
        if (relativePath.startsWith(source)) {
          return true;
        }
      }
    }
    return false;
  }

  @NotNull
  public Set<String> getDependencies() {
    return m_deps;
  }

  @Nullable
  public String getJsxVersion() {
    return m_jsxVersion;
  }

  public String @NotNull [] getPpx() {
    return m_ppx;
  }

  public void setUseExternalAsSource(boolean useExternalAsSource) {
    m_useExternalAsSource = useExternalAsSource;
  }
}
