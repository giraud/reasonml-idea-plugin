## BuckleScript Settings

Settings are per project.

You can find them in `Project settings | Languages & Frameworks | Reason`: 

![](img/settings.png)

See also: [Project Types - BuckleScript Projects](https://github.com/reasonml-editor/reasonml-idea-plugin/wiki/Project-Types#bucklescript-projects)

### Working Directory Resolution
The working directory is no longer specified via settings. Instead, 
it is determined by searching the project for a `bsconfig.json` nearest
to the active file in the editor. If there aren't any active files,
then the `bsconfig.json` nearest to the project root will be used.
