---
id: plugin-development
title: Plugin Development
sidebar_label: Plugin Development
slug: /contributing/plugin-development
---

# Plugin Development

## Prepare your environment

1. install the plugin prerequisites (from [intellij documentation](http://www.jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support/prerequisites.html))
   1. `git clone https://github.com/JetBrains/intellij-community.git`
   1. install psiviewer plugin
   1. configure an IntelliJ Platform SDK
   1. attach the Community Edition source files to the SDK

## Import

1. clone the plugin project
1. import project from external model, choose gradle
1. select ```use gradle 'wrapper' task configuration``` for easier setup
1. Set project sdk to intellij platform sdk

## Run

You can launch a new idea instance with the run ide gradle task

![](../../static/img/run_ide.png)
