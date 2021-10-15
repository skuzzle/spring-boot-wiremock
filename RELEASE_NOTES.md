[![Maven Central](https://img.shields.io/static/v1?label=MavenCentral&message=0.0.17&color=blue)](https://search.maven.org/artifact/de.skuzzle.springboot.test/spring-boot-wiremock-parent/0.0.17/jar) [![JavaDoc](https://img.shields.io/static/v1?label=JavaDoc&message=0.0.17&color=orange)](http://www.javadoc.io/doc/de.skuzzle.springboot.test/spring-boot-wiremock-parent/0.0.17)

### Features:
* Add `@Response.withJsonBody` which allows to specify a json response body and automatically sets the `Content-Type` to `application/json` 

### Bug Fixes:
* [#10](https://github.com/skuzzle/spring-boot-wiremock/issues/10) Remove `DirtiesContext` annotation
* [#11](https://github.com/skuzzle/spring-boot-wiremock/issues/11) Replaced `TestExecutionListener` with `ContextCustomizer`

### Changes:
* [#6](https://github.com/skuzzle/spring-boot-wiremock/issues/6) Remove deprecated attribute `HttpStub.wrapAround` (Deprecated since `0.0.12`). Use `HttpStub.onLastResponse` instead.
* [#6](https://github.com/skuzzle/spring-boot-wiremock/issues/6) Remove deprecated attribute `WithWiremock.httpPort` (Deprecated since `0.0.15`). Use `WithWiremock.fixedHttpPort` or `WithWiremock.randomHttpPort` instead.
* [#6](https://github.com/skuzzle/spring-boot-wiremock/issues/6) Remove deprecated attribute `WithWiremock.httpsPort` (Deprecated since `0.0.15`). Use `WithWiremock.fixedHttpsPort` or `WithWiremock.randomHttpsPort` instead.

### Maven Central coordinates for this release

```xml
<dependency>
    <groupId>de.skuzzle.springboot.test</groupId>
    <artifactId>spring-boot-wiremock-parent</artifactId>
    <version>0.0.17</version>
    <scope>test</scope>
</dependency>
```

### Gradle coordinates for this release

```
testImplementation 'de.skuzzle.springboot.test:spring-boot-wiremock-parent:0.0.17'
```