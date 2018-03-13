#!/bin/sh
java -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -Dfile.encoding=UTF-8 -jar sbt-launch.jar "$@"
