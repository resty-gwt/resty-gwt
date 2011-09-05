RestyGWT Copyright Notices 
==========================

Copyright 2009-2010 FuseSource Corp.


Update Version In Bash
======================

::

    grep -lr SNAPSHOT .|grep -v .git|while read f; do echo "$f"; sed -i "" 's/1\.2-SEC\.12-SNAPSHOT/1.2-SEC.13-SNAPSHOT/g' "$f"; done


check::

    ack --all --ignore-dir target SNAPSHOT .
