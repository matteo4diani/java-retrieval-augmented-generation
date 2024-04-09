package dev.sashacorp.javarag.shell;

import java.util.UUID;

import dev.sashacorp.javarag.langchain.OllamaChatService;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class CodeLlamaRagCommands extends AbstractShellComponent {
    public static final String BYE = "/bye";

    private final OllamaChatService ollamaChatService;
    private final ContextService contextService;
    private final TerminalService terminal;

    CodeLlamaRagCommands(
            OllamaChatService ollamaChatService,
            ContextService contextService,
            TerminalService terminalService
    ) {
        this.ollamaChatService = ollamaChatService;
        this.contextService = contextService;
        this.terminal = terminalService;
    }

    @ShellMethod(key = "chat", value = "Start to chat with a GitHub repository.", group = "RAG Chat")
    public String chat(
            @ShellOption(
                    defaultValue = "matteo4diani/java-retrieval-augmented-generation",
                    help = "The GitHub repository you want to explore."
            ) String repository
    ) {

        contextService.setup(repository);

        terminal.printWithUserPrompt(terminal.getReadyMessage());
        terminal.newLine();

        StringInput component = new StringInput(getTerminal());

        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());

        StringInput.StringInputContext context;

        var chatId = UUID.randomUUID();

        do {
            context = component.run(StringInput.StringInputContext.empty());

            if (shouldContinue(context)) {
                terminal.printWithAssistantPrompt(
                        ollamaChatService.answer(
                                chatId.toString(),
                                context.getResultValue()
                        )
                );

                terminal.newLine();
            }
        } while (shouldContinue(context));

        contextService.cleanup();

        return terminal.getGoodbyeMessage();
    }

    private boolean shouldContinue(StringInput.StringInputContext context) {
        return context.getResultValue() != null
                && !context.getResultValue().equals(BYE);
    }
}
