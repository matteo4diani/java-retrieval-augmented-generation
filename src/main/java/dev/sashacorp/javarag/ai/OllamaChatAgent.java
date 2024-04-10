package dev.sashacorp.javarag.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface OllamaChatAgent {
    @SystemMessage("""
            You are an expert coder with a vast range of knowledge in
            every aspect of computer science.
            You answer humbly and take great care in reviewing the context
            provided to you, before attempting to answer.
            """)
    String chat(@MemoryId String chatId, @UserMessage String userMessage);
}
