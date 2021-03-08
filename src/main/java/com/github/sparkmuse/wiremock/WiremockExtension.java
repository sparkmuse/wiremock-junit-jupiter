package com.github.sparkmuse.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.Options;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotatedFields;

/**
 * Main junit5 extension class
 */
public class WiremockExtension implements AfterAllCallback, TestInstancePostProcessor, AfterEachCallback {

    private static final Logger LOG = LoggerFactory.getLogger(WiremockExtension.class);

    /**
     * Stops all wiremock sever instances
     *
     * @param extensionContext text context
     */
    @Override
    public void afterAll(ExtensionContext extensionContext) {
        List<Field> serverFields = retrieveAnnotatedFields(extensionContext, Wiremock.class, WireMockServer.class);
        ExtensionContext.Store store = getStore(extensionContext);

        serverFields.stream()
                .map(field -> store.get(field.getName(), WireMockServer.class))
                .forEach(WireMockServer::stop);
    }

    /**
     * Instantiates the wiremock server instances an manages them
     *
     * @param testObject       test instance
     * @param extensionContext text context
     * @throws IllegalAccessException when cannot access field in test
     */
    @Override
    public void postProcessTestInstance(Object testObject, ExtensionContext extensionContext) throws IllegalAccessException {

        List<Field> serverFields = retrieveAnnotatedFields(extensionContext, Wiremock.class, WireMockServer.class);
        ExtensionContext.Store store = getStore(extensionContext);

        for (Field field : serverFields) {
            field.setAccessible(true);

            Function<String, WireMockServer> function =
                    getWiremockField(field, testObject) == null ?
                            key -> buildWiremockAnnotatedField(field) :
                            key -> getWiremockField(field, testObject);

            WireMockServer server = store.getOrComputeIfAbsent(field.getName(), function, WireMockServer.class);
            server.start();
            field.set(testObject, server);
        }
    }

    /**
     * Reset stubs after each test to isolate the test
     *
     * @param extensionContext text context
     */
    @Override
    public void afterEach(ExtensionContext extensionContext) {
        // because we are in afterEach, ExtensionContext actually is a MethodExtensionContext which always has non-empty parent
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        ExtensionContext classContext = extensionContext.getParent().get();
        List<Field> serverFields = retrieveAnnotatedFields(classContext, Wiremock.class, WireMockServer.class);
        ExtensionContext.Store store = getStore(classContext);

        serverFields.stream()
                .map(field -> store.get(field.getName(), WireMockServer.class))
                .forEach(WireMockServer::resetAll);
    }

    /**
     * Builds a server with the params from the annotation
     *
     * @param field annotated field
     * @return configured server
     */
    private static WireMockServer buildWiremockAnnotatedField(Field field) {
        Wiremock annotation = field.getAnnotation(Wiremock.class);
        Options options = options()
                .port(annotation.port())
                .httpsPort(annotation.httpsPort());
        return new WireMockServer(options);
    }

    /**
     * Helper method to get all annotated fields
     *
     * @param context        test context
     * @param annotationType type of the annotation
     * @param fieldType      type of the field
     * @return a list of fields found
     */
    private static List<Field> retrieveAnnotatedFields(ExtensionContext context,
                                                       Class<? extends Annotation> annotationType,
                                                       Class<?> fieldType) {
        return context.getElement()
                .map(Class.class::cast)
                .map(clazz -> findAnnotatedFields(clazz, annotationType, getFieldPredicate(fieldType)))
                .orElse(Collections.emptyList());
    }

    /**
     * Check if field is assignable
     *
     * @param fieldType type of the field
     * @return true/false
     */
    private static Predicate<Field> getFieldPredicate(Class<?> fieldType) {
        return field -> fieldType.isAssignableFrom(field.getType());
    }

    /**
     * Get access to the store
     *
     * @param context the context
     * @return the store associated to the context
     */
    private static ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(Namespace.create(WiremockExtension.class, context.getUniqueId()));
    }

    /**
     * Gets the value from a field
     *
     * @param field      The field with the value
     * @param testObject the test object with the field
     * @return A wiremock instance
     */
    private static WireMockServer getWiremockField(Field field, Object testObject) {
        try {
            return (WireMockServer) field.get(testObject);
        } catch (IllegalAccessException e) {
            LOG.error("Cannot access field.");
        }
        return null;
    }
}
