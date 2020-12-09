---
id: faq
title: FAQ
sidebar_label: FAQ
slug: /get-started/faq
---

- **Why don't you consider using Language Server Protocol?**

  Mainly because I want a strong integration in the IDE and I need a first class support of Windows.

- **What versions does the plugin support at the moment?**

  The latest version of the plugin supports **IDEA 2020.3**.

- **How is it different than [ocaml language plugin](https://github.com/sidharthkuruvila/ocaml-ide)?**

  I wanted to have a plugin dedicated to reason and that's why I created one called ReasonML, not OCaml.
  Then I realised that I still need OCaml support and that people are asking for it.
    
  I started this plugin by studying and copying parts of the ocaml plugin, but now the 2 projects are taking different orientations, technically.
    
  I am moving away from merlin and trying to use the most of IntelliJ structures.
    
  Now, they have incompatible implementations, and different goals. 

- **Modules from `node_modules` folder are not resolved (GoTo is not working on `Array.sort`)?**

  Ensure that `node_modules` folder is not excluded from IDE index.

