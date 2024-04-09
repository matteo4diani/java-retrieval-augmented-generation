package dev.sashacorp.javarag.shell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public record ContextPromptProvider(ContextService contextService) implements PromptProvider {

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
