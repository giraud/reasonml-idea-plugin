---
id: plugin-development
title: Plugin Development
sidebar_label: Plugin Development
slug: /contributing/plugin-development
---

# Tools

When developing a functionality, two tools are very important: the PSI viewer and
the indices viewer.
 
When you start an IntelliJ instance with gradle for debuging, 
they are automatically downloaded for you and immediately available.

![PSIViewer tool](../../static/img/arch/psiviewer_01.png)

> The PsiViewer tool to inspect PsiElements of the generated tree from parser

![Îndices viewer](../../static/img/arch/indicesviewer_01.png)

> The indices viewer tool let you verify the correctness of the indexing

## Enable Debug Logs

Debug statements can be found throughout the plugin codebase.
 
Here's an example:
```java
private final static Log LOG = Log.create("my-logging-category");

...

if (LOG.isDebugEnabled()) {
  LOG.debug("Log some useful debug information here...");
}
```

These statements are disabled by default. To enable debug logs, do the following:
 1. Launch an instance of IntelliJ + the plugin via the gradle task as described above.
 2. In the newly launched instance (not your development instance) click on **Help > Diagnostic Tools > Debug Log Settings...**
 3. Enter the following, replacing `my-logging-category` with the value provided by the `Log.create(...)` instantiator:
 
    ![Log Configuration](../../static/img/enable-logging.png)
 4. Debug logs should now be enabled for that logging category. To view the logs, run **Help > Show Log in Files**
 
 > Note: you can run `tail -f idea.log` from a terminal to follow along with the log output.
 
