package de.skuzzle.springboot.test.wiremock;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import com.github.tomakehurst.wiremock.WireMockServer;

import de.skuzzle.springboot.test.wiremock.stubs.HttpStub;

/**
 * Initializes the WireMock during test class setup.
 *
 * @author Simon Taddiken
 */
class WithWiremockTestExecutionListener implements TestExecutionListener {

    private static final String CONTEXT_KEY = WithWiremockTestExecutionListener.class.getName() + "."
            + "wiremockContext";
    private static final Logger log = LoggerFactory.getLogger(WithWiremockTestExecutionListener.class);

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        final WiremockAnnotationConfiguration wiremockProps = wiremockProps(testContext);
        final WireMockServer wiremockServer = startWiremock(wiremockProps);

        createContextAndattachTo(testContext, wiremockServer, wiremockProps);

        final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) testContext
                .getApplicationContext();

        injectWiremockHostIntoProperty(applicationContext, wiremockProps, wiremockServer);
        registerWiremockServerAsBean(applicationContext, wiremockProps, wiremockServer);
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        final WiremockContext context = wiremockContextFrom(testContext);

        final WithWiremock withWiremock = context.wiremockProps.withWiremockAnnotation();
        final WireMockServer wiremockServer = context.wiremockServer;

        Stream.concat(
                determineStubs(testContext.getTestClass()),
                determineStubs(testContext.getTestMethod()))
                .forEach(stub -> StubTranslator.configureStubOn(wiremockServer, withWiremock, stub));
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        final WiremockContext context = wiremockContextFrom(testContext);
        context.wiremockServer.resetAll();
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        final WiremockContext context = wiremockContextFrom(testContext);
        context.wiremockServer.shutdown();
    }

    private WiremockAnnotationConfiguration wiremockProps(TestContext testContext) {
        final Class<?> testClass = testContext.getTestClass();

        return WiremockAnnotationConfiguration.fromAnnotatedElement(testClass);
    }

    private WireMockServer startWiremock(WiremockAnnotationConfiguration wiremockProps) {
        final WireMockServer wiremockServer = wiremockProps.createWireMockServer();
        wiremockServer.start();
        return wiremockServer;
    }

    private void injectWiremockHostIntoProperty(ConfigurableApplicationContext applicationContext,
            WiremockAnnotationConfiguration wiremockProps,
            WireMockServer wiremockServer) {

        final Map<String, String> propertiesToInject = wiremockProps.determineInjectionPropertiesFrom(wiremockServer);
        log.info("Injected properties map: {}", propertiesToInject);

        TestPropertyValues
                .of(toStringProps(propertiesToInject))
                .applyTo(applicationContext);
    }

    @Deprecated
    private Stream<String> toStringProps(Map<String, String> props) {
        // Only for compatibility to older Spring-Boot versions that do not support
        // TestPropertyValues.of(Map)
        // This method can be removed when the base-line spring-boot version is 2.4.x
        return props.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue());
    }

    private void registerWiremockServerAsBean(ConfigurableApplicationContext applicationContext,
            WiremockAnnotationConfiguration wiremockProps,
            WireMockServer wiremockServer) {
        final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.registerSingleton("wiremockServer", wiremockServer);
        beanFactory.registerSingleton("wiremockProps", wiremockProps);
    }

    private Stream<HttpStub> determineStubs(AnnotatedElement e) {
        return MergedAnnotations.from(e, SearchStrategy.TYPE_HIERARCHY_AND_ENCLOSING_CLASSES)
                .stream(HttpStub.class)
                .map(MergedAnnotation::synthesize);
    }

    private WiremockContext createContextAndattachTo(TestContext testContext, WireMockServer wireMockServer,
            WiremockAnnotationConfiguration wiremockProps) {
        final WiremockContext context = new WiremockContext(wireMockServer, wiremockProps);
        testContext.setAttribute(CONTEXT_KEY, context);
        return context;
    }

    private WiremockContext wiremockContextFrom(TestContext testContext) {
        return (WiremockContext) testContext.getAttribute(CONTEXT_KEY);
    }

    private static final class WiremockContext {
        private final WireMockServer wiremockServer;
        private final WiremockAnnotationConfiguration wiremockProps;

        private WiremockContext(WireMockServer wiremockServer, WiremockAnnotationConfiguration wiremockProps) {
            this.wiremockServer = wiremockServer;
            this.wiremockProps = wiremockProps;
        }

    }
}
