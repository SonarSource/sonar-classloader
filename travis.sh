#!/bin/bash

set -euo pipefail

function strongEcho {
  echo ""
  echo "================ $1 ================="
}

if [ "$TRAVIS_PULL_REQUEST" != "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then
  strongEcho 'Build and analyze commit in master'
  mvn org.jacoco:jacoco-maven-plugin:prepare-agent verify sonar:sonar \
    -Pcoverage-per-test \
    -Dmaven.test.redirectTestOutputToFile=false \
    -Dsonar.host.url=$SONAR_HOST_URL \
    -Dsonar.login=$SONAR_TOKEN \
    -B -e -V

elif [ "$TRAVIS_PULL_REQUEST" != "false" ] && [ "$TRAVIS_SECURE_ENV_VARS" == "true" ]; then
  strongEcho 'Build and analyze pull request'
  mvn org.jacoco:jacoco-maven-plugin:prepare-agent verify sonar:sonar \
    -Pcoverage-per-test \
    -Dmaven.test.redirectTestOutputToFile=false \
    -Dsonar.analysis.mode=issues \
    -Dsonar.github.pullRequest=$TRAVIS_PULL_REQUEST \
    -Dsonar.github.repository=$TRAVIS_REPO_SLUG \
    -Dsonar.github.oauth=$GITHUB_TOKEN \
    -Dsonar.host.url=$SONAR_HOST_URL \
    -Dsonar.login=$SONAR_TOKEN \
    -B -e -V

else
  strongEcho 'Build, no analysis'
  # Build branch, without any analysis

  # No need for Maven goal "install" as the generated JAR file does not need to be installed
  # in Maven local repository
  mvn verify -Dmaven.test.redirectTestOutputToFile=false -B -e -V
fi
