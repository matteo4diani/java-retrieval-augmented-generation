package dev.sashacorp.javarag.langchain;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfiguration {
    private static final String COLLECTION_NAME = "repositories";
    private static final String OLLAMA_HOST = "http://localhost:11434";
    private static final String MODEL_NAME = "codellama";
    private static final Collections.Distance DISTANCE = Collections.Distance.Cosine;
    private static final int DIMENSION = 384;

    private static final String QDRANT_HOST = "localhost";
    private static final int QDRANT_PORT = 6334;
    private static final int QDRANT_MAX_RESULTS = 2;
    private static final double QDRANT_MIN_SCORE = 0.5;

    private static final int CHAT_MEMORY_MAX_MESSAGES = 5;

    @Bean
    QdrantClient qdrantClient() {
        return new QdrantClient(
                QdrantGrpcClient
                        .newBuilder(
                                QDRANT_HOST,
                                QDRANT_PORT,
                                false
                        )
                        .build()
        );
    }

    @Bean
    QdrantService qdrantService(QdrantClient qdrantClient) {
        return new QdrantService(qdrantClient);
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore(
            QdrantService qdrantService
    ) {
        Collections.VectorParams vectorParams = Collections.VectorParams
                .newBuilder()
                .setDistance(DISTANCE)
                .setSize(DIMENSION)
                .build();

        qdrantService.recreateCollection(COLLECTION_NAME, vectorParams);

        return QdrantEmbeddingStore
                .builder()
                .host(QDRANT_HOST)
                .port(QDRANT_PORT)
                .collectionName(COLLECTION_NAME)
                .build();
    }

    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2QuantizedEmbeddingModel();
    }

    @Bean
    ContentRetriever contentRetriever(
            EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel
    ) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(QDRANT_MAX_RESULTS)
                .minScore(QDRANT_MIN_SCORE)
                .build();
    }

    @Bean
    ChatMemoryProvider chatMemoryProvider() {
        return chatId -> MessageWindowChatMemory
                .builder()
                .id(chatId)
                .maxMessages(CHAT_MEMORY_MAX_MESSAGES)
                .build();
    }

    @Bean
    ChatLanguageModel chatLanguageModel() {
        return OllamaChatModel.builder().baseUrl(OLLAMA_HOST).modelName(MODEL_NAME).build();
    }

    @Bean
    OllamaChatAgent ollamaChatAgent(
            ChatLanguageModel chatLanguageModel,
            ChatMemoryProvider chatMemoryProvider,
            ContentRetriever contentRetriever
    ) {
        return AiServices
                .builder(OllamaChatAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(chatMemoryProvider)
                .contentRetriever(contentRetriever)
                .build();
    }
}
