package de.skuzzle.springboot.test.wiremock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.event.AfterTestExecutionEvent;
import org.springframework.test.context.event.BeforeTestExecutionEvent;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.google.common.base.Preconditions;

/**
 * Sets up the WireMock server and integrates it with the Spring
 * {@link ApplicationContext}.
 *
 * @author Simon Taddiken
 */
class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        final WiremockAnnotationProps wiremockProps = WiremockAnnotationProps.from(applicationContext);
        final WireMockServer wiremockServer = startWiremock(wiremockProps);

        injectWiremockHostIntoProperty(applicationContext, wiremockProps, wiremockServer);
        registerWiremockServerAsBean(applicationContext, wiremockProps, wiremockServer);
        addLifecycleEvents(applicationContext, wiremockServer);
    }

    private WireMockServer startWiremock(WiremockAnnotationProps wiremockProps) {
        final WireMockConfiguration wiremockConfig = wiremockProps.createWiremockConfig();
        final WireMockServer wiremockServer = new WireMockServer(wiremockConfig);
        wiremockServer.start();
        return wiremockServer;
    }

    private void injectWiremockHostIntoProperty(ConfigurableApplicationContext applicationContext,
            WiremockAnnotationProps wiremockProps,
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
        }

        if (isHttpsEnabled) {
            final String injectHttpsPropertyName = wiremockProps.getInjectHttpsHostPropertyName();
            final String httpsHost = String.format("https://localhost:%d", wiremockServer.httpsPort());
            if (!injectHttpsPropertyName.isEmpty()) {
                props.put(injectHttpsPropertyName, httpsHost);
            }
        }

        TestPropertyValues
                .of(props)
                .applyTo(applicationContext);
    }

    private void registerWiremockServerAsBean(ConfigurableApplicationContext applicationContext,
            WiremockAnnotationProps wiremockProps,
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

                final HttpStub[] stubsOnClass = e.getTestContext().getTestClass().getAnnotationsByType(HttpStub.class);
                final HttpStub[] stubsOnMethod = e.getTestContext().getTestMethod()
                        .getAnnotationsByType(HttpStub.class);

                Arrays.stream(stubsOnClass).forEach(stub -> StubTranslator.configureStubOn(wiremockServer, stub));
                Arrays.stream(stubsOnMethod).forEach(stub -> StubTranslator.configureStubOn(wiremockServer, stub));
            }
            if (applicationEvent instanceof AfterTestExecutionEvent) {
                wiremockServer.resetAll();
            }
            if (applicationEvent instanceof ContextClosedEvent) {
                wiremockServer.stop();
            }
        });
    }
}
