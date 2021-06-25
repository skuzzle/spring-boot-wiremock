<!-- This file is auto generated during release from readme/README.md -->


[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.skuzzle.springboot.test/spring-boot-wiremock/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.skuzzle.springboot.test/spring-boot-wiremock)
[![Coverage Status](https://coveralls.io/repos/github/skuzzle/spring-boot-wiremock/badge.svg?branch=main)](https://coveralls.io/github/skuzzle/spring-boot-wiremock?branch=main)
[![Twitter Follow](https://img.shields.io/twitter/follow/skuzzleOSS.svg?style=social)](https://twitter.com/skuzzleOSS)

# spring-boot-wiremock
(This is **not** an official extension from the Spring Team!)

The easiest way to setup a [WireMock](http://wiremock.org/)  server in your Spring-Boot tests with *JUnit5*
- [x] Run WireMock server on random port
- [x] Inject WireMock hosts (http and https) as spring application property
- [x] Easily setup server- and client side SSL
- [x] Define simple stubs using annotations

```xml
<dependency>
    <groupId>de.skuzzle.springboot.test</groupId>
    <artifactId>spring-boot-wiremock</artifactId>
    <version>0.0.9-SNAPSHOT</version>
</dependency>
```

## Quick start
All you need to do is to add the `@WithWiremock` annotation to your Spring-Boot test. The annotation has some 
configuration options but the most notable one is `injectHttpHostInto`.

```java
@SpringBootTest
@WithWiremock(injectHttpHostInto = "your.application.serviceUrl")
public class WiremockTest {

    @Value("${your.application.serviceUrl}")
    private String serviceUrl;
    @Autowired
    private WireMockServer wiremock;

    @Test
    void testWithExplicitStub() throws Exception {
        // Use standard WireMock API for minimum coupling to this library
        wiremock.stubFor(WireMock.get("/")
                .willReturn(aResponse().withStatus(200)));

        final ResponseEntity<Object> response = new RestTemplate()
                .getForEntity(serviceUrl, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @HttpStub(
        onRequest = @Request(withMethod = "GET"), 
        respond = @Response(withStatus = HttpStatus.OK)
    )
    void testWithAnnotationStub() throws Exception {
        // Make full use of this library by defining stubs using annotations

        final ResponseEntity<Object> response = new RestTemplate()
                .getForEntity(serviceUrl, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```
Injecting the host into the application context happens _before_ any bean instances are created and the injected 
property values takes precedence over any other, for example statically configured value. This means, in most cases the 
extension works out of the box with your current context configuration.

## Rationale
[WireMock](http://wiremock.org/) is an awesome library for mocking HTTP endpoints in unit tests. However, it can be 
quite cumbersome to integrate with Spring-Boot: when you manually manage the `WireMockServer` from within the test,
there is hardly a chance to use its random base url during Bean creation. That often results in the weirdest stunts of
Spring context configuration in order to somehow inject the mock location. For example, your client under test might 
use the `RestTemplate` and you decide to make it a mutable field in order to replace it in your test with an instance
that knows the WireMock's location.

In a perfect world, you would not need to touch your existing context configuration for just injecting a mock. Consider
the `@MockBean` annotation that allows to simply replace an already configured Bean with a mock. This works without a 
hassle and involves no stunts like defining a new Bean with same type and `@Primary` annotation or manually replacing
an injected instance using a setter.

The `@WithWiremock` annotation works just like that: It sets up a WireMock server early enough, so that its base url
can be injected into the Spring application properties, simply replacing an existing value. 

## Compatibility
- [x] Requires Java 11
- [x] Tested against Spring-Boot `2.3.12.RELEASE, 2.4.7, 2.5.1`

## Changelog

### Version 0.0.9
* [Add] Possibility to set a stub's priority
* [Add] Allow to define annotation stubs on inherited super classes and interfaces of the test class
* [Add] Allow to define annotation stubs using meta-annotated custom annotations
* [Fix] Possibility to place multiple stubs on the test class (missing `target = { ..., ElementType.TYPE }` on `HttpStubs`) 

### Version 0.0.8
* Allow to configure consecutive responses for the same request

### Version 0.0.7
* Compatibility to older Spring-Boot versions
* Remove note about Junit 5 being required. This library actually isn't tied to a specific testing framework 

### Version 0.0.6
* Improve JavaDoc
* Add automatic module name to jar manifest

### Version 0.0.5
* Improve JavaDoc
* Improve configuration consistency checks
* Allow `@HttpStub` on test class itself (instead of only on test method)
* Allow to set _status message_ on mock response
* Allow to configure WireMock _scenarios_ for stateful request matching using annotations

### Version 0.0.4
* Skipped by accident 🤡

### Version 0.0.3
* Renamed `SimpleStub` to `HttpStub` and split into multiple annotations
* `HttpStatus` enum is now used for defining the stubbed response status
* Match _any_ HTTP method by default (instead of _GET_)
* Allow to define different matchers for params, cookies, headers and body using prefixes like `eq:` or `containing:`

### Version 0.0.2
* Support multiple `@SimpleStub` instances per test method
* Allow to stub authentication and response headers via `@SimpleStub`
* Fix bug with unresolvable test keystore locations

### Version 0.0.1
* Initial prototype
