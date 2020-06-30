package com.github.sparkmuse.wiremock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Wiremock {

    int DYNAMIC_PORT = 0;

    int port() default 8080;

    int httpsPort() default -1;
}
