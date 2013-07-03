gru
===

Download the latest release here:

https://github.com/grugithub/gru/releases

Unzip the contents and create a new environment variable named GRU\_HOME that points to that folder.

Example:
GRU\_HOME=/java/apps/gru-0.0.2

Add the new folder to your path:

PATH=%GRU\_HOME%\bin;%PATH%

Now you are ready to use gru.

In a terminal window, enter the root folder of your project and enter gru.  Gru will search in that folder for a "gru" subfolder and load any plugins found.  It will also search in that folder and any of its children for java files to process.

Maven Dependency
================

If you would like to use the built-in plugins for Gru, you can include the gru-annotations dependency and add any of the pre-packaged annotations in your project.

```xml
        <dependency>
            <groupId>com.github.gru</groupId>
            <artifactId>gru-annotations</artifactId>
            <version>0.0.1</version>
            <scope>provided</scope>
        </dependency>
```
