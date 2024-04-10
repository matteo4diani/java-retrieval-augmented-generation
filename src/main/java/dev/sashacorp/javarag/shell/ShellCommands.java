package dev.sashacorp.javarag.shell;

import java.util.UUID;

import dev.sashacorp.javarag.ai.LangChainChatAgent;
import dev.sashacorp.javarag.context.ContextService;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class ShellCommands extends AbstractShellComponent {
    public static final String BYE = "/bye";

    private final LangChainChatAgent langChainChatAgent;
    private final ContextService context;
    private final TerminalService terminal;

    ShellCommands(
            LangChainChatAgent langChainChatAgent,
            ContextService context,
            TerminalService terminal
    ) {
        this.langChainChatAgent = langChainChatAgent;
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
                        langChainChatAgent.chat(
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
