# Update the version
mvn versions:set -DnewVersion=1.1.$(git rev-list HEAD --count)

# Deploy
mvn clean deploy --settings .travis/settings.xml -DskipTests=true -B -U -Prelease,ossrh