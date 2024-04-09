package dev.sashacorp.javarag.shell;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Service;

@Service
public class ContextService {
    private final AtomicReference<String> repository = new AtomicReference<>();

    void setup(String repo) {
        repository.set(repo);
    }

    void cleanup() {
        repository.set(null);
    }

    boolean isReady() {
        return !Objects.isNull(repository.get());
    }

    String repo() {
        return repository.get();
    }
}
