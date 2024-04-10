package dev.sashacorp.javarag.shell;

import dev.sashacorp.javarag.context.ContextService;
import org.jetbrains.annotations.NotNull;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public record ShellPromptProvider(
        ContextService contextService
) implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        var isReady = contextService.isReady();

        var readyPrompt = buildPrompt(
                " repo: " + contextService.repository() + " > ",
                AttributedStyle.GREEN
        );

        var notReadyPrompt = buildPrompt(
                " repo: none > ",
                AttributedStyle.YELLOW
        );

        return isReady ? readyPrompt : notReadyPrompt;
    }

    @NotNull
    private AttributedString buildPrompt(String prompt, int foregroundColor) {
        return new AttributedString(
                prompt,
                AttributedStyle.DEFAULT.foreground(foregroundColor)
        );
    }
}
