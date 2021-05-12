[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.skuzzle.springboot.test/spring-boot-wiremock/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.skuzzle.springboot.test/spring-boot-wiremock)
[![Coverage Status](https://coveralls.io/repos/github/skuzzle/spring-boot-wiremock/badge.svg?branch=main)](https://coveralls.io/github/skuzzle/spring-boot-wiremock?branch=main)
[![Twitter Follow](https://img.shields.io/twitter/follow/skuzzleOSS.svg?style=social)](https://twitter.com/skuzzleOSS)

# spring-boot-wiremock
(This is **not** an official extension from the Spring Team!)

The easiest way to setup a [WireMock](http://wiremock.org/) server in your Spring-Boot tests with *JUnit5*
- [x] Run WireMock server on random port
- [x] Inject WireMock hosts (http and https) as spring application property
- [x] Easily setup server- and client side SSL

```xml
<dependency>
    <groupId>de.skuzzle.springboot.test</groupId>
    <artifactId>spring-boot-wiremock</artifactId>
    <version>0.0.3</version>
</dependency>
```

## Quick start
All you need to do is to add the `WithWiremock` annotation to your Spring-Boot test. The annotation has some 
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
        wiremock.stubFor(WireMock.get("/")
                .willReturn(aResponse().withStatus(200)));
        final ResponseEntity<Object> response = new RestTemplateBuilder()
                .rootUri(serviceUrl)
                .build()
                .getForEntity("/", Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    
    @Test
    @HttpStub(onRequest = @Request(withMethod = "GET"), respond = @Response(withStatus = HttpStatus.OK))
    void testWithSimpleStub() throws Exception {
        final ResponseEntity<Object> response = new RestTemplateBuilder()
                .rootUri(serviceUrl)
                .build()
                .getForEntity("/", Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```
Injecting the host into the application context happens _before_ any bean instances are created. Also the injected 
property values takes precedence over any other, for example statically configured value. This means, in most cases the 
extension works out of the box with your current context configuration.

## Compatibility
- [x] Requires Java 11
- [x] Tested against Spring-Boot `2.4.5`

## Changelog

### Version 0.0.3
* Renamed `SimpleStub` to `HttpStub` and split into multiple annotations
* `HttpStatus` enum is now used for defining the stubbed response status
* Match any HTTP method by default
* Allow to define different matchers for params, cookies, headers and body using prefixes like `eq:` or `containing:`

### Version 0.0.2
* Support multiple `@SimpleStub` instances per test method
* Allow to stub authentication and response headers via `@SimpleStub`
* Fix bug with unresolvable test keystore locations

### Version 0.0.1
* Initial prototype
