package dev.sashacorp.javarag.context;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import dev.sashacorp.javarag.data.DocumentService;
import dev.sashacorp.javarag.data.GitService;
import org.jline.terminal.Terminal;
import org.springframework.stereotype.Service;

@Service
public class ContextService {
    private final AtomicReference<String> repository = new AtomicReference<>();

    private final GitService gitService;

    private final DocumentService documentService;

    private final Terminal terminal;


    public ContextService(
            GitService gitService,
            DocumentService documentService,
            Terminal terminal
    ) {
        this.gitService = gitService;
        this.documentService = documentService;
        this.terminal = terminal;
    }

    public String repository() {
        return repository.get();
    }

    public boolean setup(String repo) {
        repository.set(repo);

        var isCloned = gitService.cloneRepo(repo);

        if (!isCloned) {
            cleanup();
            return false;
        }

        try {
            documentService.ingestDocuments(repo);
        } catch (RuntimeException e) {
            cleanup();
            terminal.writer().println(" ⚠️  Exception occurred while ingesting documents for repo '" + repo + "'");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void cleanup() {
        repository.set(null);
    }

    public boolean isReady() {
        return !Objects.isNull(repository.get());
    }
}
