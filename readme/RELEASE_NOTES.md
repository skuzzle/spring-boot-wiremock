[![Maven Central](https://img.shields.io/static/v1?label=MavenCentral&message=${project.version}&color=blue)](https://search.maven.org/artifact/${project.groupId}/${project.artifactId}/${project.version}/jar) [![JavaDoc](https://img.shields.io/static/v1?label=JavaDoc&message=${project.version}&color=orange)](http://www.javadoc.io/doc/${project.groupId}/${project.artifactId}/${project.version})

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
    <groupId>${project.groupId}</groupId>
    <artifactId>${project.artifactId}</artifactId>
    <version>${project.version}</version>
    <scope>test</scope>
</dependency>
```

### Gradle coordinates for this release

```
testImplementation '${project.groupId}:${project.artifactId}:${project.version}'
```