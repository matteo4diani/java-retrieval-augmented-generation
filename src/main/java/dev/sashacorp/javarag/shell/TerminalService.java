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

    public String getContextualPrompt() {
        return contextualPromptProvider.getPrompt().toAnsi(terminal);
    }

    public void printWithUserPrompt(String toPrint) {
        printLine(
                getContextualPrompt() + toPrint
        );
    }

    public AttributedString getAssistantPrompt() {
        return new AttributedString(
                "🦙 CodeLlama: ",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN)
        );
    }

    public void printWithAssistantPrompt(String toPrint) {
        printLine(getAssistantPrompt().toAnsi(terminal) + toPrint);
    }

    public String getReadyMessage() {
        return "🚀 You're ready to chat with the Code Llama 🦙 Input '/bye' to quit the current chat.";
    }

    public String getGoodbyeMessage() {
        return "\n" + getContextualPrompt() + "👋 See you soon 🦙";
    }

    public String getErrorMessage() {
        return "\n" + getContextualPrompt() + "🚨 Something went wrong, try again 😿";
    }

}
