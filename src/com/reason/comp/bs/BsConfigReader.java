package com.reason.comp.bs;

import static jpsplugin.com.reason.StringUtil.toFirstUpper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.intellij.openapi.vfs.VirtualFile;
import jpsplugin.com.reason.FileUtil;
import gnu.trove.THashSet;
import java.util.*;
import java.util.regex.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BsConfigReader {

  private static final Pattern NORMALIZE = Pattern.compile(",$");

  @NotNull
  public static BsConfig read(@NotNull VirtualFile bsConfigFile) {
    return read(bsConfigFile, false);
  }

  @NotNull
  public static BsConfig read(@NotNull VirtualFile bsConfigFile, boolean useExternalAsSource) {
    BsConfig config = parse(FileUtil.readFileContent(bsConfigFile));
    config.setRootFile(bsConfigFile.getParent());
    config.setUseExternalAsSource(useExternalAsSource);
    return config;
  }

  @NotNull
  static BsConfig parse(@NotNull String content) {
    String normalizedContent =
        NORMALIZE
            .matcher(content)
            .replaceAll("")
            .replaceAll(",[\\s\\n]*]", "]")
            .replaceAll(",[\\s\\n]*}", "}");

    JsonElement topElement = JsonParser.parseString(normalizedContent);

    if (topElement.isJsonObject()) {
      JsonObject top = topElement.getAsJsonObject();

      String name = "";
      JsonPrimitive nameProp = top.getAsJsonPrimitive("name");
      if (nameProp != null && nameProp.isString()) {
        name = nameProp.getAsString();
      }

      String namespace = null;
      JsonPrimitive namespaceProp = top.getAsJsonPrimitive("namespace");
      if (namespaceProp != null) {
        if (namespaceProp.isBoolean()) {
          boolean hasNamespace = namespaceProp.getAsBoolean();
          namespace = hasNamespace ? toNamespace(name) : null;
        } else if (namespaceProp.isString()) {
          namespace = namespaceProp.getAsString();
        }
      }

      String jsxVersion = null;
      JsonElement reason = top.get("reason");
      JsonObject reasonProp =
          reason != null && reason.isJsonObject() ? top.getAsJsonObject("reason") : null;
      if (reasonProp != null) {
        JsonPrimitive jsxProp = reasonProp.getAsJsonPrimitive("react-jsx");
        if (jsxProp != null && jsxProp.isNumber()) {
          jsxVersion = Integer.toString(jsxProp.getAsInt());
        }
      }

      List<String> ppx = new ArrayList<>();
      JsonArray ppxProp = top.getAsJsonArray("ppx-flags");
      if (ppxProp != null) {
        for (JsonElement item : ppxProp) {
          if (item.isJsonPrimitive() && ((JsonPrimitive) item).isString()) {
            String itemValue = item.getAsString();
            if (!itemValue.isEmpty()) {
              ppx.add(itemValue);
            }
          }
        }
      }

      // bs-platform/bsconfig.json
      Set<String> externals = new THashSet<>();
      JsonArray extIncludes = top.getAsJsonArray("bs-external-includes");
      if (extIncludes != null) {
        for (JsonElement item : extIncludes) {
          if (item.isJsonPrimitive() && ((JsonPrimitive) item).isString()) {
            externals.add(item.getAsString());
          }
        }
      }
      externals.add("lib/ocaml"); // Because of Belt !

      Set<String> sources = new THashSet<>();
      Set<String> devSources = new THashSet<>();

      JsonElement srcProp = top.get("sources");
      if (srcProp != null) {
        readSources(srcProp, "", sources);
        readSources(srcProp, "dev", devSources);
      }

      Set<String> deps = new THashSet<>();
      JsonArray depsProp = top.getAsJsonArray("bs-dependencies");
      if (depsProp != null) {
        for (JsonElement item : depsProp) {
          if (item.isJsonPrimitive() && ((JsonPrimitive) item).isString()) {
            String itemValue = item.getAsString();
            if (!itemValue.isEmpty()) {
              deps.add(itemValue);
            }
          }
        }
      }

      return new BsConfig(name, namespace, jsxVersion, sources, devSources, externals, deps, ppx);
    }

    throw new RuntimeException("Not a Bucklescript config");
  }

  @NotNull
  private static String toNamespace(@NotNull String name) {
    StringBuilder result = new StringBuilder(name.replaceAll("_", ""));

    String[] tokens = result.toString().split("[-@/]");
    result = new StringBuilder(toFirstUpper(tokens[0]));
    if (1 < tokens.length) {
      for (int i = 1; i < tokens.length; i++) {
        result.append(toFirstUpper(tokens[i]));
      }
    }

    return result.toString();
  }

  private static void readSources(
      @NotNull JsonElement value, @NotNull String type, @NotNull Set<String> sources) {
    if (value.isJsonPrimitive() && ((JsonPrimitive) value).isString()) {
      if (type.isEmpty()) {
        sources.add(value.getAsString());
      }
    } else if (value.isJsonObject()) {
      assert value instanceof JsonObject;
      String src = parseSourceItem((JsonObject) value, type);
      if (src != null) {
        sources.add(src);
      }
    } else if (value.isJsonArray()) {
      for (JsonElement item : value.getAsJsonArray()) {
        if (item.isJsonPrimitive()) {
          if (((JsonPrimitive) item).isString() && type.isEmpty()) {
            sources.add(item.getAsString());
          }
        } else if (item.isJsonObject()) {
          String src = parseSourceItem((JsonObject) item, type);
          if (src != null) {
            sources.add(src);
          }
        }
      }
    }
  }

  @Nullable
  private static String parseSourceItem(@NotNull JsonObject obj, @NotNull String type) {
    JsonPrimitive srcProp = obj.getAsJsonPrimitive("dir");
    if (srcProp != null) {
      JsonPrimitive typeProp = obj.getAsJsonPrimitive("type");
      String typeValue = typeProp != null && typeProp.isString() ? typeProp.getAsString() : "";
      if (type.equals(typeValue)) {
        if (srcProp.isString()) {
          return srcProp.getAsString();
        }
      }
    }
    return null;
  }
}
