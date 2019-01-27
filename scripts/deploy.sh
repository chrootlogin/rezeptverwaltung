#!/usr/bin/env bash
set -e

export REZEPTVERWALTUNG_VERSION="$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)"

echo "Uploading jar..."
curl -T "target/rezeptverwaltung-$REZEPTVERWALTUNG_VERSION.jar" -u "$BINTRAY_USER:$BINTRAY_API_KEY" -H "X-Bintray-Publish:1" -H "X-Bintray-Package:linux" -H "X-Bintray-Version:latest-$(date +%Y-%m-%d)" -H "X-Bintray-Override:1" "https://api.bintray.com/content/rootlogin/rezeptverwaltung/latest-$(date +%Y-%m-%d)/rezeptverwaltung.jar"

echo "Uploading exe..."
curl -T "target/rezeptverwaltung-$REZEPTVERWALTUNG_VERSION.exe" -u "$BINTRAY_USER:$BINTRAY_API_KEY" -H "X-Bintray-Publish:1" -H "X-Bintray-Package:windows" -H "X-Bintray-Version:latest-$(date +%Y-%m-%d)" -H "X-Bintray-Override:1" "https://api.bintray.com/content/rootlogin/rezeptverwaltung/latest-$(date +%Y-%m-%d)/rezeptverwaltung.exe"
