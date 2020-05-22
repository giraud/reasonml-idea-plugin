# Design decisions

#### Language Server Protocol, or the lack of

The plugin is not using LSP and there is no plan to use it in the future. I'll try to explain 
the reasoning behind that decision here.

First, plain text editors (VSCode, Textmate, VIM, etc.) and IDE (Jetbrains editors) are
two very different categories. Text editors need aditional mechanisms to be able to create
a nice developer experience, including adding semantic to the sources, adding referencing of
symbols for nice navigation, documentation, etc, caching and indexing for fast response.

On the other side, an IDE - as the name implies (Integrated) - already has these behaviors
embedded in the software. With regard to Jetbrains, many man-hours have been invested through
years of development into these exact problems of indexing, referencing, etc. They have their own 
structures (PSIElements) that you must follow if you want to use the full power of the IDE.
If you do so, you get a lot of functionalities for free.

Of course, the drawback of not using LSP is that creating a functional plugin needs more work.

Another reason is that the goal of the plugin is to work out of the box as much as possible
in the most cross platform way, it means that Windows users will have the same DX than Linux
or OSX users.

The focus of the plugin is to work at much as possible at the source level.
In a second pass, to improve results, we also use information from compiled typed trees (the cmt files).

Finally, these decisions give us the ability to provide multiple languages implementations easily:
the plugin works at ReasonML and OCaml source code, and embed Dune, ML4, MLG, MLL, MLY syntaxes.

#### Language

The plugin is entirely written in Java (only version 8 is supported for now).
Kotlin was not selected for two reasons: Java might be easier for people who
want to contribute occasionally, and Kotlin workflow is much slower than Java.

# Tools

When developing a functionality, two tools are very important: the PsiViewer and
the indices viewer.
 
When you start an IntelliJ instance with gradle for debuging, 
they are automatically downloaded for you and immediately available.

[[/img/arch/psiviewer_01.png]]

> The PsiViewer tool to inspect PsiElements of the generated tree from parser

[[/img/arch/indicesviewer_01.png]]

> The indices viewer tool let you verify the correctness of the indexing

# Project organisation

The project is built using the [gradle IntelliJ plugin](https://github.com/JetBrains/gradle-intellij-plugin).
The `build.gradle` can be found at the root of the project directory.

Each version of the IntelliJ is developed in it's own branch (ex: 191-2019.1, 192-2019.2, etc.).
The main branch is the version just below the latest version (ex: if 2020.1 is the latest released,
the master branch contains the 2019.3 version).

You'll find a `jps-plugin` sub project in the sources.
It was initialy created to integrate bucklescript or dune compilation with the normal build process of Idea,
see [external build process.](https://www.jetbrains.org/intellij/sdk/docs/reference_guide/frameworks_and_external_apis/external_builder_api.html)

That work has been canceled for lack of time, but `jps-plugin` is still used for shared elements. 

Structure of the sources is:
- `bs`, `esy`, `dune` contains source code of the corresponding compilers
- `hints` contains source related to rincewind process
- `ide` contains all editor functionalities
- `lang` contains parser code, PSI definitions, stubs, etc. for the different languages

# Lexer / Parser

## PsiElement

To know more about Program Structure Interface, go to [Jetbrains SDK doc.](https://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi.html)

Not only every element of a source code must be described as a PSIElement, but also any element that 
could be used in one of the IDE functionality. For example, all files are also PSI elements: this is needed
to get functionalities like navigation, refactoring, etc.

[[img/arch/psi_hierarchy.png]]

> Everything is a PSIElement, even the files

Every parser must create a tree of PSIElements.

You will see three different types in the code: ORTokenElementType, ORCompositeElementType, and types that inherit from 
IStubElementType.

[[img/arch/types.png]]

> TODO: explain

### Lexer

The lexer is automatically generated from the [JFlex](https://www.jflex.de/) 
file `com\reason\lang\ReasonML.flex`.
 
> If you want to update the generated code, you can `ctrl+shift+g` in the file.

The lexer is used a lot in a performance critical path and must contain the minimum 
of logic to be the most efficient possible.

`ReasonML.flex` is used for the ReasonML language and the OCaml language.
`Dune.flex` (in the same directory) is the lexer used for Dune files.

### Parser

The ReasonML/OCaml parser is entirely coded manually, 
it doesn't use a [Grammar-Kit](https://github.com/JetBrains/Grammar-Kit) grammar.

The first versions of the parser were coded using Grammar-Kit, but we reconsidered
that option due to many problems. The recovery mechanism was very difficult 
to use, and debuging a grammar kit generated parser was not satisfying. Handling two different
languages was also challenging. Plus we wanted to add some semantic and context during the parsing
to help desambiguate the syntax (unfortunaltely, that point is not really used with the new parser
 neither).

[[docs/arch/parser2.png]]

> Generated types are defined in an abstract common class and implemented in two different subclasses.



# Other

Indexing
Performance, stubs
Resolving: qname finder, psifinder
fakemodule
rincewind
