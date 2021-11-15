package jpsplugin.com.reason;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.wsl.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public abstract class OCamlExecutable {
    private static final Log LOG = Log.create("ocaml");

    protected String myId;

    public static @NotNull OCamlExecutable getExecutable(@Nullable String opamRootPath, @Nullable String cygwinBash) {
        if (opamRootPath == null) {
            return new OCamlExecutable.Unknown();
        }

        if (Platform.isWindows()) {
            Pair<String, WSLDistribution> pair = OCamlExecutable.parseWslPath(opamRootPath.replace("/", "\\"));
            if (pair != null) {
                if (pair.second == null) {
                    LOG.debug("Opam home not found", opamRootPath);
                    return new OCamlExecutable.Unknown();
                } else {
                    return new OCamlExecutable.Wsl(pair.second);
                }
            }
        }

        return new OCamlExecutable.Local(cygwinBash);
    }

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

        public Wsl(@NotNull WSLDistribution distribution) {
            m_distribution = distribution;
            myId = "wsl-" + distribution.getId();
        }

        public @Nullable String convertPath(@NotNull Path path) {
            // 'C:\Users\file.txt' -> '/mnt/c/Users/file.txt'
            String wslPath = m_distribution.getWslPath(path.toString());
            if (wslPath != null) {
                return wslPath;
            }

            // '\\wsl$\_ubuntu\home\_user\file.txt' -> '/home/user/file.txt'
            Path uncRootPath = m_distribution.getUNCRootPath();
            if (FileUtil.isAncestor(uncRootPath.toFile(), path.toFile(), false)) {
                return StringUtil.trimStart(
                        FileUtil.toSystemIndependentName(path.toString()),
                        FileUtil.toSystemIndependentName(uncRootPath.toString()));
            }

            return path.toString();
        }

        @Override
        @NotNull
        public GeneralCommandLine patchCommandLine(@NotNull GeneralCommandLine commandLine, @Nullable String pathToBinary,
                                                   boolean login, @NotNull Project project) {
            String exe = commandLine.getExePath();
            commandLine.setExePath(pathToBinary == null ? exe : convertPath(new File(pathToBinary + "/" + exe).toPath()));

            try {
                return m_distribution.patchCommandLine(commandLine, project, new WSLCommandLineOptions());
            } catch (ExecutionException e) {
                throw new IllegalStateException("Cannot patch command line for WSL", e);
            }
        }

        @Override
        public @NotNull String toString() {
            return m_distribution.getPresentableName();
        }
    }

    public static class Local extends OCamlExecutable {
        private final String myCygwinBash;

        public Local(@Nullable String cygwinBash) {
            myId = "local";
            myCygwinBash = cygwinBash;
        }

        @Override
        public @NotNull GeneralCommandLine patchCommandLine(@NotNull GeneralCommandLine commandLine, @Nullable String pathToBinary, boolean login, @NotNull Project project) {
            ParametersList parametersList = commandLine.getParametersList();
            List<String> realParamsList = parametersList.getList();
            boolean isCygwin = myCygwinBash != null;

            String extension = Platform.isWindows() && isCygwin ? Platform.WINDOWS_EXECUTABLE_SUFFIX : "";
            String exe = commandLine.getExePath() + extension;
            String exePath = pathToBinary == null ? exe : pathToBinary + "/" + exe;

            if (isCygwin) {
                commandLine.setExePath(myCygwinBash);
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

            LOG.debug("[" + myId + (isCygwin ? "/cygwin" : "") + "] " + "Patched as: " + commandLine.getCommandLineString());

            return commandLine;
        }

        @Override
        public String toString() {
            return myId;
        }
    }

    public static class Unknown extends OCamlExecutable {
        public Unknown() {
            myId = "wsl-unknown";
        }

        @Override
        public @NotNull GeneralCommandLine patchCommandLine(@NotNull GeneralCommandLine commandLine,
                                                            String pathToBinary, boolean login,
                                                            @NotNull Project project) {
            throw new RuntimeException("Unknown WSL distribution");
        }

        @Override
        public String toString() {
            return myId;
        }
    }
}
