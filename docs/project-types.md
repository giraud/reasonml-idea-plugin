# Supported Project Types
_**Preview** - This document needs review and is subject to change_

Currently, three project types are supported:
1. Dune
2. BuckleScript
3. Esy (Beta)

Project types are auto-detected and a single IDEA project may contain multiple project types. An IDEA project can even contain multiple of the same type of project (mono-repo).

# Project Detection
Projects are auto-detected but may require additional setup. Detection is based on the presence of certain project configuration files. These are outlined below.

## Dune Projects
Dune projects currently require the most setup. If a `dune-project` or `dune` file is present in your project then you should be prompted to create a Dune Facet. This Facet allows you to supply additional project information such as the OCaml SDK location on your system.

## BuckleScript Projects
BuckleScript projects are detected based on the presence of a `bsconfig.json` configuration file. If a BuckleScript configuration file is present, BuckleScript support will be enabled. This can be verified by the presence of a BuckleScript tool window icon in IDEA.

## Esy Projects 
Esy projects are detected based on the presence of `package.json` file with an `"esy": {...}` property. If an Esy configuration file is present, Esy support will be enabled. This can be verified by the presence of an Esy tool window icon in IDEA.
