#!/usr/bin/bash

echo "Syncing  versions..."

if [ "$1" == "" ]; then
  echo "Use an option [major, minor, bug]"
  exit 0
fi

# Get the current version
CURRENT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
IFS='.'
read -ra ARRAY <<<"$CURRENT_VERSION"

MAJOR="${ARRAY[0]}"
MINOR="${ARRAY[1]}"
BUG="${ARRAY[2]}"

NEW_VERSION=""
# Increase the version accordingly
if [ "$1" == "major" ]; then
  NEW_VERSION="$((MAJOR + 1)).0.0"

elif [ "$1" == "minor" ]; then
  NEW_VERSION="$MAJOR.$((MINOR + 1)).0"

elif [ "$1" == "bug" ]; then
  NEW_VERSION="$MAJOR.$MINOR.$((BUG + 1))"

else
  echo "Unknown option [major, minor, bug]"
  exit 0
fi

echo "$NEW_VERSION"

# Check tag in remote
CHECK=$(git ls-remote -t origin "refs/tags/v$NEW_VERSION")
if [ "$CHECK" != "" ]; then
  echo "Tag $CHECK already exists in remote"
  echo "*** Aborted ***"
  echo "Fix issues and try again."
fi

# Check tag in local
CHECK_LOCAL=$(git tag -l | grep "$NEW_VERSION")
echo "$CHECK_LOCAL"
if [ "$CHECK_LOCAL" != "" ]; then
  echo "Tag $CHECK_LOCAL already exists in local"
  echo "*** Aborted ***"
  echo "Fix issues and try again."
fi

# Set the new version
mvn versions:set -DnewVersion="$NEW_VERSION" -DgenerateBackupPoms=false

# Replace the version in the readme
sed -i '' -e "s/${CURRENT_VERSION}/${NEW_VERSION}/g" README.md

echo ""
echo ""
echo "Do not forget to:"
echo "1) Commit the version changes"
echo "2) Create a new tag"
echo "   -  git tag -a v$NEW_VERSION -m FANCY_MESSAGE_HERE"
echo "3) Push the tag"
echo "   -  git push --tags"
echo "4) Complete the Release in Github:"
echo "   https://github.com/sparkmuse/wiremock-junit-jupiter/releases"
echo ""
