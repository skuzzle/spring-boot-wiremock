<!-- This file is auto generated during release from readme/README.md -->

[![Maven Central](https://img.shields.io/static/v1?label=MavenCentral&message=0.0.18&color=blue)](https://search.maven.org/artifact/de.skuzzle.springboot.test/spring-boot-wiremock/0.0.18/jar)
[![JavaDoc](https://img.shields.io/static/v1?label=JavaDoc&message=0.0.18&color=orange)](http://www.javadoc.io/doc/de.skuzzle.springboot.test/spring-boot-wiremock/0.0.18)
[![Coverage Status](https://coveralls.io/repos/github/skuzzle/spring-boot-wiremock/badge.svg?branch=main)](https://coveralls.io/github/skuzzle/spring-boot-wiremock?branch=main)
[![Twitter Follow](https://img.shields.io/twitter/follow/skuzzleOSS.svg?style=social)](https://twitter.com/skuzzleOSS)

# spring-boot-wiremock
_This is **not** an official extension from the Spring Team!_ (Though one exists as part of the 
[spring-cloud](https://cloud.spring.io/spring-cloud-contract/reference/html/project-features.html#features-wiremock) 
project).

_The easiest way to setup a [WireMock](http://wiremock.org/)  server in your Spring-Boot tests._
- [x] Run WireMock server on random port
- [x] Inject WireMock hosts (http and https) as spring application property
- [x] Easily setup server- and client side SSL
- [x] Declarative stubs using annotations

```xml
<dependency>
    <groupId>de.skuzzle.springboot.test</groupId>
    <artifactId>spring-boot-wiremock</artifactId>
    <version>0.0.18</version>
    <scope>test</scope>
</dependency>
```

```
testImplementation 'de.skuzzle.springboot.test:spring-boot-wiremock:0.0.18'
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

You can see more annotation stubbing examples in 
[this](https://github.com/skuzzle/spring-boot-wiremock/blob/main/src/test/java/de/skuzzle/springboot/test/wiremock/TestHttpStub.java) 
test class.

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
- [x] Tested against Spring-Boot `2.2.13.RELEASE, 2.3.12.RELEASE, 2.4.11, 2.5.5`
- [x] Tested against WireMock `2.27.2`

## Usage

### WireMock based stubbing
If you set up WireMock using `@WithWireMock` the server instance is made available as bean in the Spring 
`ApplicationContext`. It can thus be injected into the test class like this:

```java
@WithWiremock(...)
@SpringBootTest
public class WiremockTest {

    @Autowired
    private WireMockServer wiremock;
}
```

You can then use the normal WireMock API in your test methods to define stubs and verifications.

### Annotation based stubbing
If you opt-in to use annotation based stubbing provided by this library you gain the advantages of full declarative 
stubbing and easily reusable stubs.

> **Warning**: Please note that using annotation based stubbing will make it harder to get rid of this library from your 
> code base in the future. You should consider to only use WireMock based stubbing to reduce coupling to this library.

Not all WireMock features (e.g. verifications) are available in annotation based stubbing. It is always possible though 
to combine annotation based stubs with plain WireMock based stubs as describe above.

#### Simple stubs
You can define a simple stub by annotating your test/test class with `@HttpStub`. If you specify no further attributes,
the mock will now respond with `200 OK` for every request it receives. Note that all additional attributes are optional.

Here is a more sophisticated stub example:
```java
@HttpStub(
    onRequest = @Request(
            withMethod = "POST",
            toUrlPath = "/endpoint",
            withQueryParameters = "param=matching:[a-z]+",
            containingHeaders = "Request-Header=eq:value",
            containingCookies = "sessionId=containing:123456",
            withBody = "containing:Just a body",
            authenticatedBy = @Auth(
                    basicAuthUsername = "username",
                    basicAuthPassword = "password")),
    respond = @Response(
            withStatus = HttpStatus.CREATED,
            withBody = "Hello World",
            withContentType = "application/text",
            withHeaders = "Response-Header=value"))
```

#### String matching
All stub request attributes that expect a String value optionally take a matcher prefix like shown in the above example.
The following prefixes are supported:
| Prefix             | Operation |
|--------------------|-----------|
|`eq:`               | Comparison using `String.equals` |
|`eqIgnoreCase:`     | Comparison using `String.equalsIgnoreCase` |
|`eqToJson:`         | Interpretes the strings as json |
|`eqToXml`           | Interpretes the strings as xml |
|`matching:`         | Comparison using the provided regex pattern |
|`notMatching:`      | Comparison using the provided regex pattern but negates the result |
|`matchingJsonPath:` | Interpretes the string as json and matches it against the provided json path |
|`matchingXPath:`    | Interpretes the string as xml and matches it against the provided xpath |
|`containing:`       | Comparison using `String.contains` |

No prefix always results in a comparison using `String.equals`.

#### Multiple responses
It is possible to define multiple responses that will be returned by the stub when a stub is matched by consecutive 
requests. Internally this feature will create a WireMock scenario, thus you can not combine multiple responses and 
explicit scenario creation using `Request.scenario`.

```java
@HttpStub(
    respond = {
            @Response(withStatus = HttpStatus.CREATED),
            @Response(withStatus = HttpStatus.OK),
            @Response(withStatus = HttpStatus.ACCEPTED)
    })
```
When stubbing multiple responses you can define what happens when the last response has been returned using 
`HttpStub.onLastResponse` with the following options:

| `onLastResponse`         | Behavior |
|--------------------------|----------|
|`WrapAround.RETURN_ERROR` | Default behavior. Mock will answer with a `403` code after the last stubbed response |
|`WrapAround.START_OVER`   | After the last response the mock will start over and answer with the first stubbed response |
|`WrapAround.REPEAT`       | The mock keeps returning the last stubbed response |

```java
@HttpStub(
    // ...
    respond = {
        // ...
    },
    onLastResponse = WrapAround.REPEAT;
)
```

#### Sharing stubs
It is possible to share stubs among multiple tests. You can either define your stubs on a super class or an interface 
implemented by your test class. However, the preferred way of sharing stubs is to create a new annotation which 
is meta-annotated with all the stubs (and optionally also with `@WithWiremock`) like in the following example:

```java
@Retention(RUNTIME)
@Target(TYPE)
@WithWiremock(injectHttpHostInto = "sample-service.url")
@HttpStub(onRequest = @Request(toUrl = "/info"),
        respond = @Response(withStatus = HttpStatus.OK, withStatusMessage = "Everything is Ok"))
@HttpStub(onRequest = @Request(toUrl = "/submit/entity", withMethod = "PUT"), respond = {
        @Response(withStatus = HttpStatus.CREATED, withStatusMessage = "Entity created"),
        @Response(withStatus = HttpStatus.OK, withStatusMessage = "Entity already exists")
})
public @interface WithSampleServiceMock {

}
```

You can now easily reuse the complete mock definition in any `SpringBootTest`:
```java
@SpringBootTest
@WithSampleServiceMock
public class MetaAnnotatedTest {

    @Value("${sample-service.url}")
    private String sampleServiceUrl;
    
    // ...
}
```

