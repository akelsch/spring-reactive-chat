package de.htwsaar.vs.chat;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Convenience annotation to disable CSRF.
 *
 * @author Arthur Kelsch
 * @see DisableCsrfSecurityConfiguration
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DisableCsrfSecurityConfiguration.class)
public @interface DisableWebFluxSecurityCsrf {
}
