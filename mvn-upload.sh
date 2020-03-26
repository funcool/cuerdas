#!/bin/sh
mvn deploy:deploy-file -Dfile=target/cuerdas.jar -DpomFile=pom.xml -DrepositoryId=clojars -Durl=https://clojars.org/repo/
