package dev.sashacorp.javarag.langchain;

import org.springframework.stereotype.Service;

@Service
public class OllamaChatService {
    private final OllamaChatAgent ollamaChatAgent;

    public OllamaChatService(
            OllamaChatAgent ollamaChatAgent
    ) {
        this.ollamaChatAgent = ollamaChatAgent;
    }

    public String answer(String chatId, String question) {
        return ollamaChatAgent.chat(chatId, question);
    }
}
