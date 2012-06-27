# Resty-GWT - Devbliss

This is a fork from the original resty-gwt (https://github.com/chirino/resty-gwt) by Hiram Chirino.
The purpose of this fork is to provide the original resty-gwt and add feature-patches that resty-gwt is missing so far. The rule of thumb in this project is: do not commit the patches "into" resty, do commit the patches "ONTO" resty gwt (please read the update section). This means, whenever chirinos resty-gwt is updated, we want to put our patches on top of that and build a devbliss resty-gwt version. 


## Repository
This repository has two major branches (master & develop respectively) and 'n' minor branches.

 * The master branch is the copy of original chirino repo and should be kept up-to-date. 
 * The develop branch has a directory "patches" which contains the patches (quelle surprise?) and 
a script for the update process. 
 * The 'n' branches were hopefully created by the update script and will probably are named like "patch_YEARMONTHDAY_TIME". Whenever the chirino/master was updated by some commit a new devbliss resty-gwt version with our feature patches should be build.


## Preparation 
        git clone git@github.com:devbliss/resty-gwt.git
        git remote add chirino https://github.com/chirino/resty-gwt.git

## Update Process to build & deploy a new resty-gwt version

        cd your/path/to/resty-gwt
        patches/update_resty_gwt

When everything looks fine, then continue with

 * Copy the new branch name
 * open hudson job "ui-resty-gwt-devbliss"
   * paste your branch name
 * start build
   * if build was successful you'll find a *.jar on the nexus 


## Features
So far we created four feature patches. 

##### IMPORTANT: The patches have to be applied in same order they are listed in this readme.

### Feature 1: Don't Be Evil

File: patches/0\_dont\_be_evil.patch

A feature to avoid Cross-Site-Forgery (XSFR).
From a backend we receive JSON responses with a little prefix. This prefix makes it harder to steal our data via 
script-tags on websites with a different origin. 
For further information read this very nice discussion on StackOverflow 
(http://stackoverflow.com/questions/2669690/why-does-google-append-while1-in-front-of-their-json-responses).


### Feature 2: Json Raw Decoder Interceptor

File: patches/1\_interceptor.patch

This feature allows us to process the json responses BEFORE they are encoded/decoded via a annotation and a given interceptor.


### Feature 3: CORS

File: patches/2\_cors.patch

Resty-GWT does not provide an easy way to make single SSL calls. So we wrote this little enhancement.

### Feature 4: SafeHtml

File: patches/3\_safe_html.patch

SafeHtml can be used to avoid the execution of scripts by escaping the "evil" characters.
Since we have a lot of user-generated content which comes from an "untrusted" backend we wanted
to provide an easy and safe way for the frontend developers to avoid cross-site-scripting (XSS).
Therefore we added the functionality to have SafeHtml-Types in ours dtos, which will be encoded/decoded correctly.


