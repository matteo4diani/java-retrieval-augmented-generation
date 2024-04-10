package dev.sashacorp.javarag.shell;

import java.util.UUID;

import dev.sashacorp.javarag.ai.OllamaChatAgent;
import dev.sashacorp.javarag.context.ContextService;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class CodeLlamaRagCommands extends AbstractShellComponent {
    public static final String BYE = "/bye";

    private final OllamaChatAgent ollamaChatAgent;
    private final ContextService context;

    private final TerminalService terminal;

    CodeLlamaRagCommands(
            OllamaChatAgent ollamaChatAgent,
            ContextService context,
            TerminalService terminal
    ) {
        this.ollamaChatAgent = ollamaChatAgent;
        this.context = context;
        this.terminal = terminal;
    }

    @ShellMethod(key = "chat", value = "Start to chat with a GitHub repository.", group = "RAG Chat")
    public String chat(
            @ShellOption(
                    defaultValue = "matteo4diani/java-retrieval-augmented-generation",
                    help = "The GitHub repository you want to explore."
            ) String repository
    ) {

        boolean isReady = context.setup(repository);

        if (!isReady) {
            return terminal.getErrorMessage();
        }

        terminal.printWithUserPrompt(terminal.getReadyMessage());
        terminal.newLine();

        StringInput component = new StringInput(getTerminal());

        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());

        StringInput.StringInputContext stringInputContext;

        var chatId = UUID.randomUUID();

        do {
            stringInputContext = component.run(StringInput.StringInputContext.empty());

            if (shouldContinue(stringInputContext)) {
                terminal.printWithAssistantPrompt(
                        ollamaChatAgent.chat(
                                chatId.toString(),
                                stringInputContext.getResultValue()
                        )
                );

                terminal.newLine();
            }
        } while (shouldContinue(stringInputContext));

        context.cleanup();

        return terminal.getGoodbyeMessage();
    }

    private boolean shouldContinue(StringInput.StringInputContext context) {
        return context.getResultValue() != null
                && !context.getResultValue().equals(BYE);
    }
}
