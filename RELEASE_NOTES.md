* Improve documentation
* [Change] Deprecated `HttpStub.wrapAround` and introduced `HttpStub.onLastResponse` with new enum `WrapAround`.
* [Add] `WrapAround.REPEAT` which will repeat the last response on every subsequent request.
* [Add] Allow to globally define required authentication via `WithWiremock.withGlobalAuthentication`

Maven Central coordinates for this release:

```xml
<dependency>
    <groupId>de.skuzzle.springboot.test</groupId>
    <artifactId>spring-boot-wiremock</artifactId>
    <version>0.0.13-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```