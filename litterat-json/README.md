
# Litterat:json (Java JSON reader and writer)

[![GitHub](https://img.shields.io/github/license/litterat/pep-java)](https://github.com/litterat/litterat-json/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.litterat/litterat-json.svg)](https://search.maven.org/search?q=io.litterat.litterat-json)
[![badge-jdk](https://img.shields.io/badge/jdk-11-green.svg)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![Follow Litterat](https://img.shields.io/twitter/follow/litterat_io.svg?style=social)](https://twitter.com/litterat_io)
[![Follow Oobles](https://img.shields.io/twitter/follow/oobles.svg?style=social)](https://twitter.com/oobles)

NOTE: In development as part of Litterat currently in single project at https://github.com/litterat/litterat

This is an implementation of a JSON reader and writer (decoder/encoder) for Java 11 that uses the [Litterat-pep](https://github.com/litterat/pep-java) library. Litterat-pep is used to tag and prepare Java classes for serialization. It uses the GSON tokenizer and combines it with Litterat-pep to provide a small/fast JSON library that reads/writes full JSON messages. Typical usage is as follows:

```java
// Create an instance object to be written to JSON
Point p1 = new Point(1,2);

// write to array.
String json = JsonMapper.toJson(test);

// Create the object from the values
Point p2 = JsonMapper.fromJson(json, Point.class);
```

## Maven dependencies

__NOTE__ Not yet published.

Library is available from the [Maven repository](https://mvnrepository.com/artifact/io.litterat/litterat-json) using dependency:

```
<dependency>
  <groupId>io.litterat</groupId>
  <artifactId>litterat-json</artifactId>
  <version>1.0.0</version>
</dependency>
```

or for Gradle

```
// https://mvnrepository.com/artifact/io.litterat/litterat-pep
implementation group: 'io.litterat', name: 'litterat-json', version: '1.0.0'
```

## Building

Gradle 6.5 has been used for building the library. The library has been designed for Java 11 but can possibly be used in earlier versions.


## License

Litterat-json is available under the Apache 2 License. Please see the LICENSE file for more information.

## Copyright

Copyright (c) 2020, Live Media Pty. Ltd. All Rights Reserved.

JSON parser/tokenizer is from GSON and is Copyright (C) 2010 Google Inc.
