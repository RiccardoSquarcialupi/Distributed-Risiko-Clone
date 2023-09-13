To pull updates exec:

`git pull --recurse-submodules`

If you want to avoid using the *--recurse-submodules* option, you can configure git as follow:

`git config --global submodule.recurse true`

From now on you just need to `git pull` to update the entire project.