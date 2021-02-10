package com.reason.bs;

import java.util.*;
import java.util.stream.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Collections.emptyList;

/**
 * REASON format (8.2.0):
 * ----------------------
 * <p>
 * g_pkg_flg = -bs-package-name bs-basic
 * src_root_dir = U:\reason\projects\bs-basic
 * bsc = "U:\reason\projects\bs-basic\node_modules\bs-platform\win32\bsc.exe"
 * bsdep = "U:\reason\projects\bs-basic\node_modules\bs-platform\win32\bsb_helper.exe"
 * warnings =
 * bsc_flags =
 * ppx_flags =
 * g_dpkg_incls =
 * g_ns =
 * g_lib_incls = -I src -I "U:\reason\projects\bs-basic\node_modules\reason-react\lib\ocaml"
 * rule build_ast_from_re
 * command = $bsc  $warnings -bs-jsx 3 $bsc_flags -o $out -bs-syntax-only -bs-binary-ast $in
 * description = [34mBuilding[39m [2m${out}[22m
 * build  src\Foo.reast : build_ast_from_re $src_root_dir\src\Foo.re
 * rule mk_deps
 * command = $bsdep -hash 20de222cbd4694f33717b5139235334c $g_ns $in
 * restat = 1
 * description = [34mBuilding[39m [2m${out}[22m
 * build  src\Foo.d : mk_deps src\Foo.reast
 * rule ml_cmj_cmi
 * command = $bsc $g_pkg_flg $g_lib_incls $warnings $bsc_flags -o $out $in
 * dyndep = $in_e.d
 * restat = 1
 * description = [34mBuilding[39m [2m${out}[22m
 * build  src\Foo.cmj |  src\Foo.cmi $src_root_dir\lib\js\src\Foo.js : ml_cmj_cmi src\Foo.reast ||  src\Foo.d
 * g_pkg_flg = $g_pkg_flg  -bs-package-output commonjs:lib\js\src
 * <p>
 * <p>
 * RESCRIPT format (8.4.0):
 * ----------------
 * <p>
 * rescript = 1
 * g_finger := U:\reason\projects\bs-basic\node_modules\reason-react\lib\ocaml\install.stamp
 * rule astj
 * command = "U:\reason\projects\bs-basic\node_modules\bs-platform\win32\bsc.exe"  -bs-v 8.4.2 -bs-jsx 3  -absname -bs-ast -o $out $i
 * o src\Foo.ast : astj ..\..\src\Foo.re
 * rule deps
 * command = "U:\reason\projects\bs-basic\node_modules\bs-platform\win32\bsb_helper.exe" -hash 20de222cbd4694f33717b5139235334c $in
 * restat = 1
 * o src\Foo.d : deps src\Foo.ast
 * rule mij
 * command = "U:\reason\projects\bs-basic\node_modules\bs-platform\win32\bsc.exe" -I src -I "U:\reason\projects\bs-basic\node_modules\reason-react\lib\ocaml"   -bs-package-name bs-basic -bs-package-output commonjs:lib\js\$in_d:.js -bs-v $g_finger $i
 * dyndep = 1
 * restat = 1
 * o src\Foo.cmj src\Foo.cmi ..\js\src\Foo.js : mij src\Foo.ast
 */
public class Ninja {
    private final @NotNull List<String> m_includes;
    private final @NotNull List<String> m_ppxIncludes;
    private final @NotNull List<String> m_pkgFlags;
    private final @NotNull List<String> m_bscFlags;

    private final @NotNull List<String> m_args = new ArrayList<>();
    private final boolean m_isRescriptFormat;

    public Ninja(@Nullable String contents) {
        m_isRescriptFormat = contents != null && contents.startsWith("rescript");
        m_includes = readIncludes(contents);
        m_ppxIncludes = readPpxIncludes(contents);
        m_pkgFlags = readPkgFlags(contents);
        m_bscFlags = readBscFlags(contents);

        if (m_isRescriptFormat) {
            m_args.addAll(extractRuleAstj(contents));
            m_args.addAll(extractedRuleMij(contents));
        }
    }

    private @NotNull List<String> extractRuleAstj(@NotNull String contents) {
        List<String> filteredTokens = new ArrayList<>();

        int ruleMijPos = contents.indexOf("rule astj");
        if (0 < ruleMijPos) {
            int commandPos = contents.indexOf("command", ruleMijPos);
            if (0 < commandPos) {
                int commandEolPos = contents.indexOf("\n", commandPos);
                String command = contents.substring(commandPos + 9, commandEolPos).trim();
                String[] tokens = command.split(" ");

                for (int i = 0; i < tokens.length; i++) {
                    String token = tokens[i];
                    if ("-ppx".equals(token)) {
                        filteredTokens.add(token);
                        filteredTokens.add(tokens[i + 1]);
                    }
                }
            }
        }
        return filteredTokens;
    }

    private @NotNull List<String> extractedRuleMij(@NotNull String contents) {
        int ruleMijPos = contents.indexOf("rule mij");
        if (0 < ruleMijPos) {
            int commandPos = contents.indexOf("command", ruleMijPos);
            if (0 < commandPos) {
                int commandEolPos = contents.indexOf("\n", commandPos);
                String command = contents.substring(commandPos + 9, commandEolPos).trim();
                String[] tokens = command.split(" ");
                List<String> filteredTokens = Arrays.stream(tokens).filter(s -> !s.isEmpty() && !"$g_finger".equals(s) && !"$i".equals(s) && !"-bs-v".equals(s) && !"-bs-package-output".equals(s) && !s.contains("$in_d:")).collect(Collectors.toList());
                filteredTokens.remove(0);
                return filteredTokens;
            }
        }
        return emptyList();
    }

    public @NotNull List<String> getArgs() {
        return m_args;
    }

    private @NotNull List<String> readIncludes(@Nullable String contents) {
        List<String> result = new ArrayList<>();
        if (contents == null) {
            return result;
        }

        int g_lib_incls = contents.indexOf("g_lib_incls");
        if (g_lib_incls >= 0) {
            int end_of_g_lib_incls = contents.indexOf("\n", g_lib_incls);
            String lineContents = contents.substring(g_lib_incls + 13, end_of_g_lib_incls).trim();
            for (String token : lineContents.split("-I\\s")) {
                String trimmedToken = token.trim();
                if (!trimmedToken.isEmpty()) {
                    if (trimmedToken.startsWith("\"")) {
                        result.add(trimmedToken.substring(1, trimmedToken.length() - 1));
                    } else {
                        result.add(trimmedToken);
                    }
                }
            }
        }

        int g_dpkg_incls = contents.indexOf("g_dpkg_incls");
        if (g_dpkg_incls >= 0) {
            int end_of_g_dpkg_incls = contents.indexOf("\n", g_dpkg_incls);
            String lineContents = contents.substring(g_dpkg_incls + 14, end_of_g_dpkg_incls).trim();
            for (String token : lineContents.split("-I\\s")) {
                String trimmedToken = token.trim();
                if (!trimmedToken.isEmpty()) {
                    if (trimmedToken.startsWith("\"")) {
                        result.add(trimmedToken.substring(1, trimmedToken.length() - 1));
                    } else {
                        result.add(trimmedToken);
                    }
                }
            }
        }

        return result;
    }

    public @NotNull List<String> readPpxIncludes(@Nullable String contents) {
        List<String> result = new ArrayList<>();
        if (contents == null) {
            return result;
        }

        int ppx_flags = contents.indexOf("ppx_flags");
        if (ppx_flags >= 0) {
            int end_of_ppx_flags = contents.indexOf("\n", ppx_flags);
            String ppxLine = contents.substring(ppx_flags + 11, end_of_ppx_flags).trim();
            String[] split = ppxLine.split("-ppx\\s");
            for (String include : split) {
                String trimmedInclude = include.trim();
                if (!trimmedInclude.isEmpty()) {
                    if (trimmedInclude.startsWith("\"")) {
                        result.add(trimmedInclude.substring(1, trimmedInclude.length() - 1));
                    } else {
                        result.add(trimmedInclude);
                    }
                }
            }
        }

        return result;
    }

    private @NotNull List<String> readPkgFlags(@Nullable String contents) {
        List<String> result = new ArrayList<>();
        if (contents == null) {
            return result;
        }

        int property = contents.indexOf("g_pkg_flg");
        if (property >= 0) {
            int end_of_property = contents.indexOf("\n", property);
            String lineContents = contents.substring(property + 11, end_of_property).trim();
            for (String tokens : lineContents.split("\\s")) {
                String trimmedToken = tokens.trim();
                if (!trimmedToken.isEmpty()) {
                    result.add(trimmedToken);
                }
            }
        }

        return result;
    }

    private @NotNull List<String> readBscFlags(@Nullable String contents) {
        List<String> result = new ArrayList<>();
        if (contents == null) {
            return result;
        }

        int property = contents.indexOf("bsc_flags");
        if (property >= 0) {
            int end_of_property = contents.indexOf("\n", property);
            String lineContents = contents.substring(property + 11, end_of_property).trim();
            for (String token : lineContents.split("\\s")) {
                String trimmedToken = token.trim();
                if (!trimmedToken.isEmpty()) {
                    result.add(trimmedToken);
                }
            }
        }

        return result;
    }

    public void addInclude(@NotNull String source) {
        m_includes.add(source);
    }

    public @NotNull List<String> getPkgFlags() {
        return m_pkgFlags;
    }

    public @NotNull List<String> getBscFlags() {
        return m_bscFlags;
    }

    public @NotNull List<String> getPpxIncludes() {
        return m_ppxIncludes;
    }

    public @NotNull List<String> getIncludes() {
        return m_includes;
    }

    public boolean isRescriptFormat() {
        return m_isRescriptFormat;
    }
}
