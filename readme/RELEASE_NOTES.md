[![Maven Central](https://img.shields.io/static/v1?label=MavenCentral&message=${project.version}&color=blue)](https://search.maven.org/artifact/${project.groupId}/${project.artifactId}/${project.version}/jar) [![JavaDoc](https://img.shields.io/static/v1?label=JavaDoc&message=${project.version}&color=orange)](http://www.javadoc.io/doc/${project.groupId}/${project.artifactId}/${project.version})

### Features:
* [#3](https://github.com/skuzzle/spring-boot-wiremock/issues/3) Allow to inject host values into multiple properties
* [#4](https://github.com/skuzzle/spring-boot-wiremock/issues/4) New properties to configure ports
* [#9](https://github.com/skuzzle/spring-boot-wiremock/issues/9) Introduce ApiGuard annotations

### Deprecations
* `WithWiremock.httpPort()` (in favor of `WithWiremock.randomHttpPort` or `WithWiremock.fixedHttpPort`)
* `WithWiremock.httpsPort()` (in favor of `WithWiremock.randomHttpsPort` or `WithWiremock.fixedHttpsPort`)

### Maven Central coordinates for this release:

```xml
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>${project.artifactId}</artifactId>
    <version>${project.version}</version>
    <scope>test</scope>
</dependency>
```

### Gradle coordinates for this release:

```
testImplementation '${project.groupId}:${project.artifactId}:${project.version}'
```