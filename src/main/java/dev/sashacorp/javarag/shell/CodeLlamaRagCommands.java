package dev.sashacorp.javarag.shell;

import java.util.UUID;

import dev.sashacorp.javarag.langchain.OllamaChatService;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
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
    private final ContextPromptProvider contextPromptProvider;

    CodeLlamaRagCommands(
            OllamaChatService ollamaChatService,
            ContextService contextService,
            ContextPromptProvider contextPromptProvider
    ) {
        this.ollamaChatService = ollamaChatService;
        this.contextService = contextService;
        this.contextPromptProvider = contextPromptProvider;
    }

    @ShellMethod(key = "chat", value = "Start to chat with a GitHub repository.", group = "RAG Chat")
    public String chat(
            @ShellOption(
                    defaultValue = "matteo4diani/java-retrieval-augmented-generation",
                    help = "The GitHub repository you want to explore."
            ) String repository
    ) {

        contextService.setup(repository);

        userPrint("🚀 You're ready to chat with the code in " + repository);

        StringInput component = new StringInput(getTerminal());

        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());

        StringInput.StringInputContext context;

        var chatId = UUID.randomUUID();

        do {
            context = component.run(StringInput.StringInputContext.empty());

            if (shouldContinue(context)) {
                assistantPrint(ollamaChatService.answer(chatId.toString(), context.getResultValue()));
                newLine();
            }
        } while (shouldContinue(context));

        contextService.cleanup();

        return getGoodbyeMessage();
    }

    private boolean shouldContinue(StringInput.StringInputContext context) {
        return context.getResultValue() != null && !context.getResultValue().equals(BYE);
    }

    private void userPrint(String toPrint) {
        getTerminal().writer().println(contextPromptProvider.getPrompt().toAnsi(getTerminal()) + toPrint);
    }

    private void newLine() {
        getTerminal().writer().println();
    }

    private void assistantPrint(String toPrint) {
        getTerminal().writer().println(getAssistantPrompt().toAnsi(getTerminal()) + toPrint);
    }

    private String getGoodbyeMessage() {
        return "\n" + contextPromptProvider.getPrompt().toAnsi(getTerminal()) + "🧹 Context cleared successfully! See you soon 🖖";
    }

    private AttributedString getAssistantPrompt() {
        return new AttributedString(
                "🦙 CodeLlama:\n",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN)
        );
    }
}
