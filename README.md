# NodePool plugin

This repository contains a Jenkins plugin to perform builds on cloud instance  nodes sourced
from [NodePool](https://docs.openstack.org/infra/nodepool/).

## Structure

The implementation consists of a listener class that creates agents (slaves) when a item with a
matching label enters the Jenkins build queue.  There is also another listener that releases each
node after it is used once.

## Releasing

The following are instructions to release a new version of the plugin so it will appear as an available plugins under `Manage Plugins` in the Jenkins UI.  For more information, see [here](https://wiki.jenkins.io/display/JENKINS/Hosting+Plugins#HostingPlugins-Releasingtojenkins-ci.org).

Steps:

1. Edit `~/.m2/settings.xml` to add your [Jenkins CI](https://accounts.jenkins.io/login) username and password.  Add this block to the XML:

  ```
  <servers>
    <server>
      <id>maven.jenkins-ci.org</id> <!-- For parent 1.397 or newer; this ID is used for historical reasons and independent of the actual host name -->
      <username>username</username>
      <password>password</password>
    </server>
  </servers>
  ```

2. Confirm the `pom.xml` in the master branch of the repository has a tag containing `SNAPSHOT`.

3. Run the maven release plugin: `mvn [-B] mvn release:prepare release:perform`
  * The -B flag is optional to accept all default choices.
 
The maven release plugin will do the following:

* Run unit tests
* Build a new plugin version
* Upload the new plugin artifact to the maven repository [here](https://repo.jenkins-ci.org/releases/org/jenkins-ci/plugins/nodepool-agents/).
* Tag git with the new plugin version.
* Update the git master branch `pom.xml` with a SNAPSHOT tag representing the next (unreleased) development version.

### Availability
* [The new plugin version will *not* be instantly available for use in the Jenkins UI under `Manage Plugins`](https://wiki.jenkins.io/display/JENKINS/Hosting+Plugins#HostingPlugins-Help!Mypluginisnotshowingupintheupdatecenter).
* It will take a maximum of **8 hours** for the new version to appear in the [json file](https://updates.jenkins-ci.org/current/update-center.json) that the UI consumes.
* Once the version is listed in the `update-center.json` file, it should be available to install in the UI.  You may need to click `Check Now` to update the list of available plugins.

### Experimental Versioning

To release an experimental version of a plugin, put *alpha* or *beta* in the version string.

To install an experimental plugin:

* Go to `Manager Plugins`
* Click `Advanced`
* Enter [http://updates.jenkins-ci.org/experimental/update-center.json](http://updates.jenkins-ci.org/experimental/update-center.json) in the `Update Site` URL field.

Full details on experimental plugins are [here](https://jenkins.io/blog/2013/09/23/experimental-plugins-update-center/).
