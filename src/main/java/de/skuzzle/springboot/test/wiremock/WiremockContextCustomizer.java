package de.skuzzle.springboot.test.wiremock;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.event.AfterTestExecutionEvent;
import org.springframework.test.context.event.BeforeTestExecutionEvent;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.base.Preconditions;

import de.skuzzle.springboot.test.wiremock.stubs.HttpStub;

final class WiremockContextCustomizer implements ContextCustomizer {

    private final WiremockAnnotationConfiguration wiremockProps;

    public WiremockContextCustomizer(WiremockAnnotationConfiguration wiremockProps) {
        Preconditions.checkArgument(wiremockProps != null, "props must not be null");
        this.wiremockProps = wiremockProps;
    }

    @Override
    public int hashCode() {
        return Objects.hash(wiremockProps);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof WiremockContextCustomizer
                && Objects.equals(wiremockProps, ((WiremockContextCustomizer) obj).wiremockProps);
    }

    @Override
    public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
        final WireMockServer server = startServer();
        injectHost(context, server);
        addLifecycleEvents(context, server);
    }

    private WireMockServer startServer() {
        final WireMockServer server = wiremockProps.createWireMockServer();
        server.start();
        return server;
    }

    private void injectHost(ConfigurableApplicationContext context, WireMockServer server) {
        final Map<String, String> propertiesToInject = wiremockProps.determineInjectionPropertiesFrom(server);
        TestPropertyValues
                .of(toStringProps(propertiesToInject))
                .applyTo(context);
    }

    private void addLifecycleEvents(ConfigurableApplicationContext applicationContext,
            WireMockServer wiremockServer) {
        applicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof BeforeTestExecutionEvent) {
                final BeforeTestExecutionEvent e = (BeforeTestExecutionEvent) applicationEvent;

                final WithWiremock withWiremock = this.wiremockProps.annotation();

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

    @Deprecated
    private Stream<String> toStringProps(Map<String, String> props) {
        // Only for compatibility to older Spring-Boot versions that do not support
        // TestPropertyValues.of(Map)
        // This method can be removed when the base-line spring-boot version is 2.4.x
        return props.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue());
    }

}
