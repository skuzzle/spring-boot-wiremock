package de.skuzzle.springboot.test.wiremock;

import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.event.AfterTestExecutionEvent;
import org.springframework.test.context.event.BeforeTestExecutionEvent;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.google.common.base.Preconditions;

import de.skuzzle.springboot.test.wiremock.stubs.HttpStub;

/**
 * Sets up the WireMock server and integrates it with the Spring
 * {@link ApplicationContext}.
 *
 * @author Simon Taddiken
 */
class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String SERVER_HTTP_HOST_PROPERTY = "wiremock.server.httpHost";
    private static final String SERVER_HTTP_PORT_PROPERTY = "wiremock.server.httpPort";
    private static final String SERVER_HTTPS_HOST_PROPERTY = "wiremock.server.httpsHost";
    private static final String SERVER_HTTPS_PORT_PROPERTY = "wiremock.server.httpsPort";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        final WiremockAnnotationConfiguration wiremockProps = WiremockAnnotationConfiguration.from(applicationContext);
        final WireMockServer wiremockServer = startWiremock(wiremockProps);

        injectWiremockHostIntoProperty(applicationContext, wiremockProps, wiremockServer);
        registerWiremockServerAsBean(applicationContext, wiremockProps, wiremockServer);
        addLifecycleEvents(applicationContext, wiremockServer);
    }

    private WireMockServer startWiremock(WiremockAnnotationConfiguration wiremockProps) {
        final WireMockConfiguration wiremockConfig = wiremockProps.createWiremockConfig();
        final WireMockServer wiremockServer = new WireMockServer(wiremockConfig);
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
            final String injectHttpPropertyName = wiremockProps.getInjectHttpHostPropertyName();
            final String httpHost = String.format("http://localhost:%d", wiremockServer.port());
            if (!injectHttpPropertyName.isEmpty()) {
                props.put(injectHttpPropertyName, httpHost);
            }
            props.put(SERVER_HTTP_HOST_PROPERTY, httpHost);
            props.put(SERVER_HTTP_PORT_PROPERTY, "" + wiremockServer.port());
        }

        if (isHttpsEnabled) {
            final String injectHttpsPropertyName = wiremockProps.getInjectHttpsHostPropertyName();
            final String httpsHost = String.format("https://localhost:%d", wiremockServer.httpsPort());
            if (!injectHttpsPropertyName.isEmpty()) {
                props.put(injectHttpsPropertyName, httpsHost);
            }
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
            WireMockServer wiremockServer) {
        applicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof BeforeTestExecutionEvent) {
                final BeforeTestExecutionEvent e = (BeforeTestExecutionEvent) applicationEvent;

                final WithWiremock withWiremock = MergedAnnotations
                        .from(e.getTestContext().getTestClass(), SearchStrategy.TYPE_HIERARCHY_AND_ENCLOSING_CLASSES)
                        .stream(WithWiremock.class)
                        .map(MergedAnnotation::synthesize)
                        .findFirst()
                        .orElseThrow();

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
