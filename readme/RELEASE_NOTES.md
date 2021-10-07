[![Maven Central](https://img.shields.io/static/v1?label=MavenCentral&message=${project.version}&color=blue)](https://search.maven.org/artifact/${project.groupId}/${project.artifactId}/${project.version}/jar) [![JavaDoc](https://img.shields.io/static/v1?label=JavaDoc&message=${project.version}&color=orange)](http://www.javadoc.io/doc/${project.groupId}/${project.artifactId}/${project.version})

### Bug Fixes
* [#2](https://github.com/skuzzle/spring-boot-wiremock/issues/2) Eventually fix locating the keystore problem 
* [#11](https://github.com/skuzzle/spring-boot-wiremock/issues/11) Fix problem with properties not being injected into `ConfigurationProperties`

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