package dev.sashacorp.javarag.ai;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import dev.sashacorp.javarag.annotations.ForOllama;
import dev.sashacorp.javarag.annotations.ForOpenAi;
import dev.sashacorp.javarag.constants.BeanQualifiers;
import dev.sashacorp.javarag.constants.Hosts;
import dev.sashacorp.javarag.constants.MetadataKeys;
import dev.sashacorp.javarag.constants.ModelParameters;
import dev.sashacorp.javarag.constants.OllamaParameters;
import dev.sashacorp.javarag.constants.QdrantParameters;
import dev.sashacorp.javarag.context.ContextService;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfiguration {

    @Value("${openai.key}")
    private String OPENAI_KEY;

    @Bean
    QdrantClient qdrantClient() {
        return new QdrantClient(
                QdrantGrpcClient
                        .newBuilder(
                                Hosts.QDRANT_HOST,
                                Hosts.QDRANT_PORT,
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
                .setDistance(ModelParameters.DISTANCE)
                .setSize(ModelParameters.DIMENSION)
                .build();

        qdrantService.recreateCollection(QdrantParameters.COLLECTION_NAME, vectorParams);

        return QdrantEmbeddingStore
                .builder()
                .host(Hosts.QDRANT_HOST)
                .port(Hosts.QDRANT_PORT)
                .collectionName(QdrantParameters.COLLECTION_NAME)
                .build();
    }

    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2QuantizedEmbeddingModel();
    }

    @Bean
    ContentRetriever contentRetriever(
            EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel,
            ContextService context
    ) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .dynamicFilter(query -> metadataKey(MetadataKeys.REPOSITORY).isEqualTo(context.repository()))
                .maxResults(ModelParameters.QDRANT_MAX_RESULTS)
                .minScore(ModelParameters.QDRANT_MIN_SCORE)
                .build();
    }

    @Bean
    ChatMemoryProvider chatMemoryProvider() {
        return chatId -> MessageWindowChatMemory
                .builder()
                .id(chatId)
                .maxMessages(ModelParameters.CHAT_MEMORY_MAX_MESSAGES)
                .build();
    }

    @Bean(BeanQualifiers.OLLAMA_LLM)
    @ForOllama
    ChatLanguageModel ollamaLanguageModel() {
        return OllamaChatModel.builder().baseUrl(Hosts.OLLAMA_HOST).modelName(OllamaParameters.OLLAMA_MODEL_NAME).build();
    }

    @Bean(BeanQualifiers.OPENAI_LLM)
    @ForOpenAi
    ChatLanguageModel openAiLanguageModel() {
        return OpenAiChatModel.withApiKey(OPENAI_KEY);
    }

    @Bean
    @ForOpenAi
    LangChainTools langchainTools() {
        return new LangChainTools();
    }

    @Bean
    @ForOllama
    LangChainChatAgent ollamaLangChainChatAgent(
            @Qualifier(BeanQualifiers.OLLAMA_LLM) ChatLanguageModel chatLanguageModel,
            ChatMemoryProvider chatMemoryProvider,
            ContentRetriever contentRetriever
    ) {
        return AiServices
                .builder(LangChainChatAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(chatMemoryProvider)
                .contentRetriever(contentRetriever)
                .build();
    }

    @Bean
    @ForOpenAi
    LangChainChatAgent openAiLangChainChatAgent(
            @Qualifier(BeanQualifiers.OPENAI_LLM) ChatLanguageModel chatLanguageModel,
            ChatMemoryProvider chatMemoryProvider,
            ContentRetriever contentRetriever,
            LangChainTools langChainTools
    ) {
        return AiServices
                .builder(LangChainChatAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(chatMemoryProvider)
                .contentRetriever(contentRetriever)
                .tools(langChainTools)
                .build();
    }
}
