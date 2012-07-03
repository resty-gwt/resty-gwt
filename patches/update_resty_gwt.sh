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
    echo "> repository remote '$UPSTREAM_REPO_NAME' not found, we're adding it!"
    git remote add $UPSTREAM_REPO_NAME $UPSTREAM_REPO;
}

# fetch updates from the original resty-gwt into master
echo "update master"
git checkout master
git fetch $UPSTREAM_REPO_NAME
git merge $UPSTREAM_REPO_NAME/master

DATE=$(date +%Y%m%d)
TIME=$(date +%H%M%S)
NAME=patched_${DATE}-${TIME}

# create new branch
git checkout -b $NAME


###############
# apply patches
###############

echo
echo "####################"
echo "patching versions: SNAPSHOT -> DEVBLISS-${DATE}"
echo "####################"
grep -lr SNAPSHOT . |\
    grep -v .git |\
    grep -v "/patches" |\
    while read f; do
        echo "$f";
        sed -i "" "s/SNAPSHOT/DEVBLISS-${DATE}-SNAPSHOT/g" "$f";
        done

echo
echo "####################"
echo "apply 'dont be evil' patch"
echo "####################"
git apply --whitespace=fix --ignore-whitespace patches/0_dont_be_evil.patch || echo "applying patch failed"
git add .
git commit -m "Don't be evil Patch"

echo
echo "####################"
echo "apply 'interceptor' patch"
echo "####################"
git apply --whitespace=fix --ignore-whitespace patches/1_interceptor.patch || echo "applying patch failed"
git add .
git commit -m "interceptor patch"

echo
echo "####################"
echo "apply 'cors' patch"
echo "####################"
git apply --whitespace=fix --ignore-whitespace patches/2_cors.patch || echo "applying patch failed"
git add .
git commit -m "Cors Patch"

echo
echo "####################"
echo "apply 'safe-html' patch"
echo "####################"
git apply --whitespace=fix --ignore-whitespace patches/3_safe_html.patch || echo "applying patch failed"
git add .
git commit -m "SafeHtml Patch"

echo
echo "####################"
echo "mvn install"
echo "####################"
mvn install

echo
echo "####################"
echo "-> publish branch $NAME"
echo "####################"
git push origin $NAME

echo
echo "#######################"
echo "= install successfull ="
echo "#######################"

# jump back to previous directory
cd $OLDPWD
