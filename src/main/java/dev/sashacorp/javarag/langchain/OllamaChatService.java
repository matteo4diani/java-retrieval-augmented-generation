package dev.sashacorp.javarag.langchain;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Service;

@Service
public class OllamaChatService {

    private final OllamaChatAgent ollamaChatAgent;

    public OllamaChatService() {
        ChatLanguageModel model =
                OllamaChatModel.builder().baseUrl("http://localhost:11434").modelName("codellama").build();

        this.ollamaChatAgent = AiServices.builder(OllamaChatAgent.class)
                         .chatLanguageModel(model)
                         .chatMemoryProvider(
                                 chatId -> MessageWindowChatMemory
                                         .builder()
                                         .id(chatId)
                                         .maxMessages(5)
                                         .build()
                         )
                         .build();
    }

    public String answer(String chatId, String question) {
        return ollamaChatAgent.chat(chatId, question);
    }
}
