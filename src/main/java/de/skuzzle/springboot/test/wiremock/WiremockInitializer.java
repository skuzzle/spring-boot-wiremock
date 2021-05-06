package de.skuzzle.springboot.test.wiremock;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.event.AfterTestExecutionEvent;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

class WiremockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        final WiremockAnnotationProps wiremockProps = WiremockAnnotationProps.from(applicationContext);
        final WireMockServer wiremockServer = startWiremock(wiremockProps);

        injectWiremockHostIntoProperty(applicationContext, wiremockProps, wiremockServer);
        registerWiremockServerAsBean(applicationContext, wiremockServer);
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

        final Map<String, String> props = new HashMap<>();
        if (!wiremockServer.getOptions().getHttpDisabled()) {
            final String injectHttpPropertyName = wiremockProps.getInjectHttpHostPropertyName();
            final String httpHost = String.format("http://localhost:%d", wiremockServer.port());
            if (!injectHttpPropertyName.isEmpty()) {
                props.put(injectHttpPropertyName, httpHost);
            }
        }
        if (wiremockServer.getOptions().httpsSettings().enabled()) {
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
            WireMockServer wiremockServer) {
        applicationContext
                .getBeanFactory()
                .registerSingleton("wiremockServer", wiremockServer);
    }

    private void addLifecycleEvents(ConfigurableApplicationContext applicationContext,
            WireMockServer wiremockServer) {
        applicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof AfterTestExecutionEvent) {
                wiremockServer.resetAll();
            }
            if (applicationEvent instanceof ContextClosedEvent) {
                wiremockServer.stop();
            }
        });
    }
}
