#!/usr/bin/env bash
javac -cp .:/opt/java/jdk1.8.0_102/lib/tools.jar CmdLineTool.java
java -cp .:/opt/java/jdk1.8.0_102/lib/tools.jar CmdLineTool $1
