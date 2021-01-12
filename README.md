# matlab-weka-package

Weka package for loader and saver for binary Matlab .mat files, using the 
[MFL](https://github.com/HebiRobotics/MFL) library.

## Options

The loader:

```
```

The saver:

```
```


## Releases

* [2021.1.12](https://github.com/fracpete/matlab-weka-package/releases/download/v2021.1.12/matlab-2021.1.12.zip)


## Maven

Use the following dependency in your `pom.xml`:

```xml
    <dependency>
      <groupId>com.github.fracpete</groupId>
      <artifactId>matlab-weka-package</artifactId>
      <version>2021.1.12</version>
      <type>jar</type>
      <exclusions>
        <exclusion>
          <groupId>nz.ac.waikato.cms.weka</groupId>
          <artifactId>weka-dev</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
```


## How to use packages

For more information on how to install the package, see:

https://waikato.github.io/weka-wiki/packages/manager/


