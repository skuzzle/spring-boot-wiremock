[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.skuzzle.springboot.test/spring-boot-wiremock/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.skuzzle.enforcer/restrict-imports-enforcer-rule)
[![Build Status](https://travis-ci.org/skuzzle/restrict-imports-enforcer-rule.svg?branch=master)](https://travis-ci.org/skuzzle/restrict-imports-enforcer-rule) 
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
    <version>0.0.1</version>
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
    void testCallWiremockWithRestTemplate() throws Exception {
        wiremock.stubFor(WireMock.get("/")
                .willReturn(aResponse().withStatus(200)));
        final ResponseEntity<Object> response = new RestTemplateBuilder()
                .rootUri(serviceUrl)
                .build()
                .getForEntity("/", Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

## Changelog

### Version 0.0.1
* Initial prototype
