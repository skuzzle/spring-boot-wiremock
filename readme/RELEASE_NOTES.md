* Improve documentation
* [Change] Deprecated `HttpStub.wrapAround` and introduced `HttpStub.onLastResponse` with new enum `WrapAround`.
* [Add] `WrapAround.REPEAT` which will repeat the last response on every subsequent request.

```xml
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>${project.artifactId}</artifactId>
    <version>${project.version}</version>
    <scope>test</scope>
</dependency>
```