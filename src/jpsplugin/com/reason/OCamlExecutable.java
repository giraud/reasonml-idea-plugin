package jpsplugin.com.reason;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.wsl.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.*;
import jpsplugin.com.reason.sdk.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public abstract class OCamlExecutable {

    private static final Log LOG = Log.create("sdk");

    protected Sdk m_odk;
    protected String m_id;

    public static @NotNull OCamlExecutable getExecutable(@Nullable Sdk odk) {
        if (odk == null) {
            return new OCamlExecutable.Unknown();
        }

        if (Platform.isWindows()) {
            String odkPath = odk.getHomePath();
            if (odkPath == null) {
                return new OCamlExecutable.Unknown();
            }

            Pair<String, WSLDistribution> pair = OCamlExecutable.parseWslPath(odkPath.replace("/", "\\"));
            if (pair != null) {
                if (pair.second == null) {
                    LOG.debug("Sdk home not found", odkPath);
                    return new OCamlExecutable.Unknown();
                } else {
                    return new OCamlExecutable.Wsl(odk, pair.second);
                }
            }
        }

        return new OCamlExecutable.Local(odk);
    }

    abstract String getPathSeparator();

    abstract String getPathVariable();

    public abstract String convertPath(Path file);

    public abstract @NotNull GeneralCommandLine patchCommandLine(@NotNull GeneralCommandLine commandLine,
                                                                 String pathToBinary, boolean login,
                                                                 @NotNull Project project);

    // From GitExecutableManager
    @Nullable
    public static Pair<String, WSLDistribution> parseWslPath(@NotNull String path) {
        if (!WSLUtil.isSystemCompatible()) {
            return null;
        }
        if (!path.startsWith(WSLDistribution.UNC_PREFIX)) {
            return null;
        }

        path = StringUtil.trimStart(path, WSLDistribution.UNC_PREFIX);
        int index = path.indexOf('\\');
        if (index == -1) {
            return null;
        }

        String distName = path.substring(0, index);
        String wslPath = FileUtil.toSystemIndependentName(path.substring(index));

        WSLDistribution distribution = WslDistributionManager.getInstance().getOrCreateDistributionByMsId(distName);

        return Pair.create(wslPath, distribution);
    }

    public static class Wsl extends OCamlExecutable {
        private final WSLDistribution m_distribution;

        public Wsl(@NotNull Sdk odk, @NotNull WSLDistribution distribution) {
            m_odk = odk;
            m_distribution = distribution;
            m_id = "wsl-" + distribution.getId();
        }

        @Override
        public String getPathSeparator() {
            return ":";
        }

        @Override
        public String getPathVariable() {
            return "$PATH";
        }

        @Override
        public String convertPath(Path path) {
            // 'C:\Users\file.txt' -> '/mnt/c/Users/file.txt'
            String wslPath = m_distribution.getWslPath(path.toString());
            if (wslPath != null) {
                return wslPath;
            }

            // '\\wsl$\_ubuntu\home\_user\file.txt' -> '/home/user/file.txt'
            File uncRoot = m_distribution.getUNCRoot();
            if (FileUtil.isAncestor(uncRoot, path.toFile(), false)) {
                return StringUtil.trimStart(
                        FileUtil.toSystemIndependentName(path.toString()),
                        FileUtil.toSystemIndependentName(uncRoot.getPath()));
            }

            return path.toString();
        }

        @Override
        @NotNull
        public GeneralCommandLine patchCommandLine(@NotNull GeneralCommandLine commandLine, @Nullable String pathToBinary,
                                                   boolean login, @NotNull Project project) {
            String exe = commandLine.getExePath();
            commandLine.setExePath(pathToBinary == null ? exe : convertPath(new File(pathToBinary + "/" + exe).toPath()));

            return m_distribution.patchCommandLine(commandLine, project, null, false);
        }

        @Override
        public String toString() {
            return m_distribution.getPresentableName();
        }
    }

    public static class Local extends OCamlExecutable {

        public Local(@Nullable Sdk odk) {
            m_id = "local";
            m_odk = odk;
        }

        @Override
        String getPathSeparator() {
            return File.pathSeparator;
        }

        @Override
        String getPathVariable() {
            return Platform.isWindows() ? "%PATH%" : "$PATH";
        }

        @Override
        public String convertPath(Path file) {
            return file.toString();
        }

        @Override
        @NotNull
        public GeneralCommandLine patchCommandLine(@NotNull GeneralCommandLine commandLine, String pathToBinary, boolean login, @NotNull Project project) {
            OCamlSdkAdditionalData odkData = (OCamlSdkAdditionalData) m_odk.getSdkAdditionalData();
            boolean isCygwin = odkData != null && odkData.isCygwin();

            ParametersList parametersList = commandLine.getParametersList();
            List<String> realParamsList = parametersList.getList();

            String extension = Platform.isWindows() && isCygwin ? Platform.WINDOWS_EXECUTABLE_SUFFIX : "";
            String exe = commandLine.getExePath() + extension;
            String exePath = pathToBinary == null ? exe : pathToBinary + "/" + exe;

            if (isCygwin) {
                LOG.debug("[" + m_id + "] Patching: " + commandLine.getCommandLineString());

                commandLine.setExePath(odkData.getCygwinBash());
                String bashParameters = StringUtil.join(ContainerUtil.prepend(realParamsList, exePath), CommandLineUtil::posixQuote, " ");

                parametersList.clearAll();
                if (login) {
                    parametersList.add("--login");
                }
                parametersList.add("-c");
                parametersList.add(bashParameters);
            } else {
                commandLine.setExePath(exePath);
            }

            LOG.debug("[" + m_id + "] " + "Patched as: " + commandLine.getCommandLineString());

            return commandLine;
        }

        @Override
        public String toString() {
            return m_id;
        }
    }

    public static class Unknown extends OCamlExecutable {
        public Unknown() {
            m_id = "wsl-unknown";
        }

        @Override
        String getPathSeparator() {
            return File.pathSeparator;
        }

        @Override
        String getPathVariable() {
            return Platform.isWindows() ? "%PATH%" : "$PATH";
        }

        @Override
        public String convertPath(Path path) {
            return path.toString();
        }

        @Override
        @NotNull
        public GeneralCommandLine patchCommandLine(@NotNull GeneralCommandLine commandLine,
                                                   String pathToBinary, boolean login,
                                                   @NotNull Project project) {
            throw new RuntimeException("Unknown WSL distribution");
        }

        @Override
        public String toString() {
            return m_id;
        }
    }
}
