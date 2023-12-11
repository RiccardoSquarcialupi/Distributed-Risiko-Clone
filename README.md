## RISIKO!
# A p2p implementation.

The first time you clone the repo you need to:
`git submodule --init --recursive`
To correctly pull submodules exec:
`git pull --recurse-submodules`
If you want to avoid using the *--recurse-submodules* option, you can configure git as follow:
`git config --global submodule.recurse true`
From now on you just need to git pull to update the entire project.