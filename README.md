# spring-boot-wiremock
(This is **not** an official extension from the Spring Team!)

The easiest way to setup a [WireMock](http://wiremock.org/) server in your Spring-Boot tests
- [x] Run WireMock server on random port
- [x] Inject WireMock hosts (http and https) as spring application property
- [x] Easily setup server- and client side SSL

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

