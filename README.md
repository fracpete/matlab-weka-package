# matlab-weka-package

Weka package for loader and saver for binary Matlab .mat files, using the 
[MFL](https://github.com/HebiRobotics/MFL) library.

## Options

The loader:

```
Usage:
	MatlabMatLoader <file.mat> [options]

Options:

-decimal <num>
	The maximum number of digits to print after the decimal
	place for numeric values (default: 6)
-entry-name <name>
	The entry name to retrieve; first if empty
	(default: )
-max-nominal-values <int>
	The maximum number of distinct values a NOMINAL attribute
	can have; beyond that it is considered a STRING attribute.
	Use -1 to always convert to NOMINAL, 0 to always convert to STRING.
	(default: 25)
```

The saver:

```
MatlabMatSaver options:

-i <the input file>
	The input file
-o <the output file>
	The output file
-entry-name-meta <name>
	The entry name to use for the header
	(default: meta)
-entry-name-data <name>
	The entry name to use for the data
	(default: data)
```


## Releases

* [2021.1.13](https://github.com/fracpete/matlab-weka-package/releases/download/v2021.1.13/matlab-2021.1.13.zip)


## Maven

Use the following dependency in your `pom.xml`:

```xml
    <dependency>
      <groupId>com.github.fracpete</groupId>
      <artifactId>matlab-weka-package</artifactId>
      <version>2021.1.13</version>
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


