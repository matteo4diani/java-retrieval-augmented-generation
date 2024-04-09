package dev.sashacorp.javarag.shell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Component;

@ShellComponent
class CodeLlamaRagCommands extends AbstractShellComponent {
    private final ContextService contextService;
    private final SetupPromptProvider setupPromptProvider;

    CodeLlamaRagCommands(
            ContextService contextService,
            SetupPromptProvider setupPromptProvider
    ) {
        this.contextService = contextService;
        this.setupPromptProvider = setupPromptProvider;
    }

    @ShellMethod(key = "chat", value = "Start to chat with a GitHub repository.", group = "RAG Chat")
    public String chat(
            @ShellOption(
                    defaultValue = "matteo4diani/java-retrieval-augmented-generation",
                    help = "The GitHub repository you want to explore."
            ) String repository
    ) {

        contextService.setup(repository);

        print("🚀 You're ready to chat with the code in " + repository);

        StringInput component = new StringInput(getTerminal());
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());

        StringInput.StringInputContext context;

        do {
            context = component.run(StringInput.StringInputContext.empty());
        } while (!context.getResultValue().equals("/bye"));

        contextService.cleanup();

        return withPrompt("🧹 Context cleared successfully!");
    }

    private void print(String toPrint) {
        getTerminal().writer().println(setupPromptProvider.getPrompt().toAnsi(getTerminal()) + toPrint);
    }

    private String withPrompt(String string) {
        return setupPromptProvider.getPrompt().toAnsi(getTerminal()) + string;
    }
}

@Component
record SetupPromptProvider(ContextService contextService) implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        var isReady = contextService.isReady();

        var readyPrompt = new AttributedString(
                " repo: " + contextService.repo() + " > ",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN)
        );

        var notReadyPrompt = new AttributedString(
                " repo: none > ",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)
        );

        return isReady ? readyPrompt : notReadyPrompt;
    }
}

