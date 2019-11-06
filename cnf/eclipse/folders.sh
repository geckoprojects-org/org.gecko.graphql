#!/bin/bash
# This script zips all deflated folders in the base-folder, where this script was started
# The generated archives will be zipped as .jar file and stored in the base-folder
# If you run it in a 'plugins' folder and have a folder named 'test_1.2.3' (meaning: 'plugins/test_1.2.3/*'), you will
# get a plugins/test_1.2.3.jar
# We use it at the Oomph Bundle Pools 'plugins' folder. We run it like this 'test@laptop:/home/test/bundle_pools/plugins$ ./folders.sh' to create 
# jars from the deflated bundles. After that we link the plugins folder into an Eclipse folder and executes bnd's 'Generate OSGi Repo. Index'.

# ignore hidden folders, that start with a dot
shopt -u dotglob
# create a folder, where all the created jar-files will be stored
#mkdir deflated
find * -prune -type d | while IFS= read -r d; do 
    echo "$d"
# switch to the folder and zip the content as jar.
    cd "$d"
    zip -r "$d".jar *
# move the jar file in the base folder and reset the path
    mv "$d".jar ../"$d".jar
    cd ..
# copy the jar into a deflated folder
#    cp "$d".jar deflated/
done
