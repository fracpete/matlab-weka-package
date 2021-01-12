How to make a release
=====================

Preparation
-----------

* Change the artifact ID in `pom.xml` to today's date, e.g.:

  ```
  2020.11.29-SNAPSHOT
  ```

* Update the version, date and URL in `Description.props` to reflect new
  version, e.g.:

  ```
  Version=2021.1.13
  Date=2021-01-13
  PackageURL=https://github.com/fracpete/matlab-weka-package/releases/download/v2021.1.13/matlab-2021.1.13.zip
  ```

* Commit/push all changes


Weka package
------------

* Run the following command to generate the package archive for version `2021.1.13`:

  ```
  ant -f build_package.xml -Dpackage=matlab-2021.1.13 clean make_package
  ```

* Create a release tag on github (v2021.1.13)
* add release notes
* upload package archive from `dist`


Maven
-----

* Run the following command to deploy the artifact:

  ```
  mvn release:clean release:prepare release:perform
  ```

* log into https://oss.sonatype.org and close/release artifacts

* After successful deployment, push the changes out:

  ```
  git push
  ````

