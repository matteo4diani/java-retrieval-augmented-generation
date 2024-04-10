package dev.sashacorp.javarag.shell;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import dev.sashacorp.javarag.git.GitService;
import org.springframework.stereotype.Service;

@Service
public class ContextService {
    private final AtomicReference<String> repository = new AtomicReference<>();
    private final GitService gitService;

    public ContextService(
            GitService gitService
    ) {
        this.gitService = gitService;
    }

    boolean setup(String repo) {
        repository.set(repo);
        var isCloned = gitService.cloneRepo(repo);

        if (!isCloned) {
            cleanup();
        }

        return isCloned;
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
