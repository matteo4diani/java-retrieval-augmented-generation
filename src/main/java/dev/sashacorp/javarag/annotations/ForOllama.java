package dev.sashacorp.javarag.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Retention(RetentionPolicy.RUNTIME)
@ConditionalOnProperty(prefix="dev.sashacorp", name="model-provider", havingValue = "ollama")
public @interface ForOllama {
}
