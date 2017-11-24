package jps.builder;

import java.io.*;
import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.builders.BuildTarget;

public class BsbSourceRootDescriptor extends BuildRootDescriptor {
    private final File m_root;
    private final BsbTarget m_target;

    public BsbSourceRootDescriptor(File root, BsbTarget target) {
        m_root = root;
        m_target = target;
    }

    @Override
    public String getRootId() {
        return m_root.getAbsolutePath();
    }

    @Override
    public File getRootFile() {
        return m_root;
    }

    @Override
    public BuildTarget<?> getTarget() {
        return m_target;
    }
}
