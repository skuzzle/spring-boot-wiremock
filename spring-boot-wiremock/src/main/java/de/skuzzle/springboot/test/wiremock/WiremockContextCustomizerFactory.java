package de.skuzzle.springboot.test.wiremock;

import java.util.List;

import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

/**
 * Bootstraps the WireMock integration using {@link WiremockContextCustomizer} if the
 * {@link WithWiremock} annotation is detected on a test class.
 *
 * @author Simon Taddiken
 */
class WiremockContextCustomizerFactory implements ContextCustomizerFactory {

    @Override
    public ContextCustomizer createContextCustomizer(Class<?> testClass,
            List<ContextConfigurationAttributes> configAttributes) {

        return WiremockAnnotationConfiguration
                .fromAnnotatedElement(testClass)
                .map(WiremockContextCustomizer::new)
                .orElse(null);
    }

}
