#!/bin/sh
# This script updates the original resty-gwt and applies our very own patches.

# stop the script on error
set -e

# jump to git repository root directory and save the current
OLDPWD=$PWD
cd $(git rev-parse --show-toplevel)

UPSTREAM_REPO_NAME=chirino
UPSTREAM_REPO=https://github.com/${UPSTREAM_REPO_NAME}/resty-gwt.git

git remote show | grep -q $UPSTREAM_REPO_NAME || {
    git remote add $UPSTREAM_REPO_NAME $UPSTREAM_REPO;
}

# fetch updates from the original resty-gwt into master
echo "update master"
git checkout master
git fetch $UPSTREAM_REPO_NAME
git merge $UPSTREAM_REPO_NAME/master

NAME=patched_$(date +%Y%m%d-%H%M%S)

# create new branch
git checkout -b $NAME


###############
# apply patches
###############

echo
echo "####################"
echo "apply 'dont be evil' patch"
echo "####################"
git apply patches/0_dont_be_evil.patch || echo "applying patch failed"
git add .
git commit -m "Don't be evil Patch"

echo
echo "####################"
echo "apply 'interceptor' patch"
echo "####################"
git apply patches/1_interceptor.patch || echo "applying patch failed"
git add .
git commit -m "interceptor patch"

echo
echo "####################"
echo "apply 'cors' patch"
echo "####################"
git apply patches/2_cors.patch || echo "applying patch failed"
git add .
git commit -m "Cors Patch"

echo
echo "####################"
echo "apply 'safe-html' patch"
echo "####################"
git apply patches/3_safe_html.patch || echo "applying patch failed"
git add .
git commit -m "SafeHtml Patch"

echo
echo "####################"
echo "mvn install"
echo "####################"
mvn install
#git push origin $name

echo
echo "####################"
echo "install successfull"
echo "-> published branch $NAME"
echo "####################"
# switch back to develop
#git checkout develop

# jump back to previous directory
cd $OLDPWD
