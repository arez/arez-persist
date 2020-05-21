# Arez-Persist

[![Build Status](https://api.travis-ci.com/arez/arez-persist.svg?branch=master)](http://travis-ci.com/arez/arez-persist)
[<img src="https://img.shields.io/maven-central/v/org.realityforge.arez.persist/arez-persist.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.realityforge.arez.persist%22)

This library provides annotation driven infrastructure for persisting observable properties on
Arez components. The library has built-in support for storing properties in memory, within a browser
session or across browser sessions but allows users to supply their own mechanisms for persisting state.

## Quick Start

The simplest way to use the library;

* add the following dependencies into the build system. i.e.

```xml
<dependency>
   <groupId>org.realityforge.arez.persist</groupId>
   <artifactId>arez-persist-core</artifactId>
   <version>0.00</version>
</dependency>
```
* To enable the annotation processor used by the framework, you need add the following
  snippet to configure the maven compiler plugin from within the `pom.xml`:

```xml
<project>
  ...
  <plugins>
    ...
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.5.1</version>
        <configuration>
          <source>1.8</source>
          <target>1.8</target>
          <useIncrementalCompilation>false</useIncrementalCompilation>
          <annotationProcessorPaths>
            <path>
               <groupId>org.realityforge.arez.persist</groupId>
               <artifactId>arez-persist-processor</artifactId>
               <version>0.00</version>
            </path>
          </annotationProcessorPaths>
        </configuration>
      </plugin>
      ...
    </plugins>
  </build>
</project>
```

* If you are using ArezPersist within a GWT application you will also need to inherit the appropriate
  GWT module in your `.gwt.xml` file. It is usually sufficient to add:

```xml
<module>
  ...
  <inherits name='arez.persist.Persist'/>
  ...
</module>
```

  If you want the framework to perform validation and invariant checking you can instead inherit
  the `Dev` module instead. The `Dev` module is very useful during development as it adds a
  level of safety and error checking, but it should not be used in production environments as it adds
  some overhead in terms of code size and execution speed. The `Dev` module can be added via:

```xml
<module>
  ...
  <inherits name='arez.persist.PersistDev'/>
  ...
</module>
```


# More Information

For more information about component, please see the [Website](https://arez.github.io/persist). For the
source code and project support please visit the [GitHub project](https://github.com/arez/arez-persist).

# Contributing

The component was released as open source so others could benefit from the project. We are thankful for any
contributions from the community. A [Code of Conduct](CODE_OF_CONDUCT.md) has been put in place and
a [Contributing](CONTRIBUTING.md) document is under development.

# License

The component is licensed under [Apache License, Version 2.0](LICENSE).
# Credit

* [Stock Software](http://www.stocksoftware.com.au/) for providing significant support in building and
  maintaining Arez-Persist.
