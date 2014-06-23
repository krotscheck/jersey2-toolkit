#!/bin/bash

case "$1" in
	start)
		mvn -B com.atlassian.maven.plugins:maven-jgitflow-plugin:release-start -DpushReleases=false
		RETVAL=1
		;;
	finish)
		mvn -B com.atlassian.maven.plugins:maven-jgitflow-plugin:release-finish -DpushReleases=true
		RETVAL=1
		;;
	*)
		echo "Usage: './release.sh start' or './release.sh finish'"
		RETVAL=0
esac

exit $RETVAL