package de.skuzzle.springboot.test.wiremock;

import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.event.AfterTestExecutionEvent;
import org.springframework.test.context.event.BeforeTestExecutionEvent;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.base.Preconditions;

import de.skuzzle.springboot.test.wiremock.stubs.HttpStub;

/**
 * Initializes the WireMock during test class setup.
 *
 * @author Simon Taddiken
 */
class WithWiremockTestExecutionListener implements TestExecutionListener {

    private static final String SERVER_HTTP_HOST_PROPERTY = "wiremock.server.httpHost";
    private static final String SERVER_HTTP_PORT_PROPERTY = "wiremock.server.httpPort";
    private static final String SERVER_HTTPS_HOST_PROPERTY = "wiremock.server.httpsHost";
    private static final String SERVER_HTTPS_PORT_PROPERTY = "wiremock.server.httpsPort";

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        final ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) testContext
                .getApplicationContext();
        final WiremockAnnotationConfiguration wiremockProps = wiremockProps(testContext);
        initializeWiremock(applicationContext, wiremockProps);
    }

    private WiremockAnnotationConfiguration wiremockProps(TestContext testContext) {
        final Class<?> testClass = testContext.getTestClass();
        final WithWiremock wwm = MergedAnnotations
                .from(testClass, SearchStrategy.TYPE_HIERARCHY_AND_ENCLOSING_CLASSES)
                .stream(WithWiremock.class)
                .map(MergedAnnotation::synthesize)
                .findFirst()
                .orElseThrow();

        return WiremockAnnotationConfiguration.from(wwm, testContext.getApplicationContext());
    }

    private void initializeWiremock(ConfigurableApplicationContext applicationContext,
            WiremockAnnotationConfiguration wiremockProps) {
        final WireMockServer wiremockServer = startWiremock(wiremockProps);

        injectWiremockHostIntoProperty(applicationContext, wiremockProps, wiremockServer);
        registerWiremockServerAsBean(applicationContext, wiremockProps, wiremockServer);
        addLifecycleEvents(applicationContext, wiremockProps, wiremockServer);
    }

    private WireMockServer startWiremock(WiremockAnnotationConfiguration wiremockProps) {
        final WireMockServer wiremockServer = wiremockProps.createWireMockServer();
        wiremockServer.start();
        return wiremockServer;
    }

    private void injectWiremockHostIntoProperty(ConfigurableApplicationContext applicationContext,
            WiremockAnnotationConfiguration wiremockProps,
            WireMockServer wiremockServer) {

        final boolean isHttpEnabled = !wiremockServer.getOptions().getHttpDisabled();
        final boolean sslOnly = wiremockProps.sslOnly();
        final boolean isHttpsEnabled = wiremockServer.getOptions().httpsSettings().enabled();
        Preconditions.checkArgument(isHttpsEnabled || !sslOnly,
                "WireMock configured for 'sslOnly' but with HTTPS disabled. Configure httpsPort with value >= 0");
        Preconditions.checkArgument(isHttpEnabled || isHttpsEnabled,
                "WireMock configured with disabled HTTP and disabled HTTPS. Please configure either httpPort or httpsPort with a value >= 0");

        final Map<String, String> props = new HashMap<>();
        if (isHttpEnabled) {
            final String httpHost = String.format("http://localhost:%d", wiremockServer.port());
            final Set<String> injectHttpPropertyNames = wiremockProps.getInjectHttpHostPropertyName();

            injectHttpPropertyNames.forEach(propertyName -> props.put(propertyName, httpHost));
            props.put(SERVER_HTTP_HOST_PROPERTY, httpHost);
            props.put(SERVER_HTTP_PORT_PROPERTY, "" + wiremockServer.port());
        }

        if (isHttpsEnabled) {
            final String httpsHost = String.format("https://localhost:%d", wiremockServer.httpsPort());
            final Set<String> injectHttpsPropertyNames = wiremockProps.getInjectHttpsHostPropertyName();

            injectHttpsPropertyNames.forEach(propertyName -> props.put(propertyName, httpsHost));
            props.put(SERVER_HTTPS_HOST_PROPERTY, httpsHost);
            props.put(SERVER_HTTPS_PORT_PROPERTY, "" + wiremockServer.httpsPort());
        }

        TestPropertyValues
                .of(toStringProps(props))
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
        final ConfigurableListableBeanFactory beanFactory = applicationContext
                .getBeanFactory();
        beanFactory.registerSingleton("wiremockServer", wiremockServer);
        beanFactory.registerSingleton("wiremockProps", wiremockProps);
    }

    private void addLifecycleEvents(ConfigurableApplicationContext applicationContext,
            WiremockAnnotationConfiguration wiremockProps,
            WireMockServer wiremockServer) {
        applicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof BeforeTestExecutionEvent) {
                final BeforeTestExecutionEvent e = (BeforeTestExecutionEvent) applicationEvent;

                final WithWiremock withWiremock = wiremockProps.withWiremockAnnotation();

                final TestContext testContext = e.getTestContext();
                Stream.concat(
                        determineStubs(testContext.getTestClass()),
                        determineStubs(testContext.getTestMethod()))
                        .forEach(stub -> StubTranslator.configureStubOn(wiremockServer, withWiremock, stub));

            }
            if (applicationEvent instanceof AfterTestExecutionEvent) {
                wiremockServer.resetAll();
            }
            if (applicationEvent instanceof ContextClosedEvent) {
                wiremockServer.stop();
            }
        });
    }

    private Stream<HttpStub> determineStubs(AnnotatedElement e) {
        return MergedAnnotations.from(e, SearchStrategy.TYPE_HIERARCHY_AND_ENCLOSING_CLASSES)
                .stream(HttpStub.class)
                .map(MergedAnnotation::synthesize);

    }
}
