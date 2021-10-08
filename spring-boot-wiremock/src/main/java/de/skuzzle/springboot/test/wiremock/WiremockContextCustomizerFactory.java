package de.skuzzle.springboot.test.wiremock;

import java.util.List;

import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

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
