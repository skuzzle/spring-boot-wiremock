[![Maven Central](https://img.shields.io/static/v1?label=MavenCentral&message=0.0.15-SNAPSHOT&color=blue)](https://search.maven.org/artifact/de.skuzzle.springboot.test/spring-boot-wiremock/0.0.15-SNAPSHOT/jar) [![JavaDoc](https://img.shields.io/static/v1?label=JavaDoc&message=0.0.15-SNAPSHOT&color=orange)](http://www.javadoc.io/doc/de.skuzzle.springboot.test/spring-boot-wiremock/0.0.15-SNAPSHOT)

### Features:
* [#3](https://github.com/skuzzle/spring-boot-wiremock/issues/3) Allow to inject host values into multiple properties
* [#4](https://github.com/skuzzle/spring-boot-wiremock/issues/4) New properties to configure ports

### Deprecations
* `WithWiremock.httpPort()` (in favor of `WithWiremock.randomHttpPort` or `WithWiremock.fixedHttpPort`)
* `WithWiremock.httpsPort()` (in favor of `WithWiremock.randomHttpsPort` or `WithWiremock.fixedHttpsPort`)

Maven Central coordinates for this release:

```xml
<dependency>
    <groupId>de.skuzzle.springboot.test</groupId>
    <artifactId>spring-boot-wiremock</artifactId>
    <version>0.0.15-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

Gradle coordinates for this release:

```
testImplementation 'de.skuzzle.springboot.test:spring-boot-wiremock:0.0.15-SNAPSHOT'
```