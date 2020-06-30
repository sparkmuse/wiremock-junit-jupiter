package com.github.sparkmuse.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.Options;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotatedFields;

/**
 * Main junit5 extension class
 */
public class WiremockExtension implements AfterAllCallback, TestInstancePostProcessor, AfterEachCallback {

    private static final Namespace NAMESPACE = Namespace.create("wiremock");

    private final Map<String, WireMockServer> servers = new HashMap<>();

    /**
     * Stops all wiremock sever instances
     *
     * @param extensionContext text context
     */
    @Override
    public void afterAll(ExtensionContext extensionContext) {
        servers.values().forEach(WireMockServer::stop);
    }

    /**
     * Instantiates the wiremock server instances an manages them
     *
     * @param testObject test instance
     * @param extensionContext text context
     * @throws IllegalAccessException when cannot access field in test
     */
    @Override
    public void postProcessTestInstance(Object testObject, ExtensionContext extensionContext) throws IllegalAccessException {

        List<Field> serverFields = retrieveAnnotatedFields(extensionContext, Wiremock.class, WireMockServer.class);

        for (Field field : serverFields) {

            field.setAccessible(true);

            if (field.get(testObject) != null && !servers.containsKey(field.getName())) {
                servers.put(field.getName(), (WireMockServer) field.get(testObject));
            }

            WireMockServer server = servers.computeIfAbsent(field.getName(), key -> buildAnnotatedServer(field));

            server.start();
            field.set(testObject, server);
        }
    }

    /**
     * Reset stubs after each test to isolate the test
     * @param extensionContext text context
     */
    @Override
    public void afterEach(ExtensionContext extensionContext) {
        servers.values().forEach(WireMockServer::resetAll);
    }

    /**
     * Builds a server with the params from the annotation
     *
     * @param field annotated field
     * @return configured server
     */
    private static WireMockServer buildAnnotatedServer(Field field) {
        Wiremock annotation = field.getAnnotation(Wiremock.class);
        Options options = options()
                .port(annotation.port())
                .httpsPort(annotation.httpsPort());
        return new WireMockServer(options);
    }

    /**
     * Helper method to get all annotated fields
     *
     * @param context test context
     * @param annotationType type of the annotation
     * @param fieldType type of the field
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
     * @param fieldType type of the field
     * @return true/false
     */
    private static Predicate<Field> getFieldPredicate(Class<?> fieldType) {
        return field -> fieldType.isAssignableFrom(field.getType());
    }
}
