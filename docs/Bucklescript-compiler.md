# How it works

When you open a project, bsb is automatically called and run a `make-world` command.

Then, each time a file is saved, bsb is automatically called with the incremental compilation mode (ie. no arguments)

If you need it, you can also manually execute a `clean-world make-world` command by using the earth icon in the bucklescript window:

![](img/earth.png)

# When files are saved ?

You can find the settings for saving files in Appearance & Behavior:

![](img/save_settings.png)

With idea, files are saved automatically, and you don't know exactly when.
But you can force a save with key shortcut: it is sometimes better because you get feedback more quickly.
