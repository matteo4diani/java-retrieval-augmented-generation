package dev.sashacorp.javarag.shell;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.stereotype.Service;

@Service
public record TerminalService(
        Terminal terminal,
        ContextualPromptProvider contextualPromptProvider
) {
    public void printLine(String toPrint) {
        terminal.writer().println(toPrint);
    }

    public void newLine() {
        terminal.writer().println();
    }

    public String contextualPrompt() {
        return contextualPromptProvider.getPrompt().toAnsi(terminal);
    }

    public void printWithUserPrompt(String toPrint) {
        printLine(
                contextualPrompt() + toPrint
        );
    }

    public void printWithAssistantPrompt(String toPrint) {
        printLine(getAssistantPrompt().toAnsi(terminal) + toPrint);
    }

    public String getGoodbyeMessage() {
        return "\n" + contextualPrompt() + "🧹 Context cleared successfully! See you soon 🖖";
    }

    public AttributedString getAssistantPrompt() {
        return new AttributedString(
                "🦙 CodeLlama:\n",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN)
        );
    }
}
