package dev.sashacorp.javarag.data;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public record DocumentService(
        EmbeddingModel embeddingModel,
        EmbeddingStore<TextSegment> embeddingStore,
        ResourceLoader resourceLoader
) {
    public static final String REPO_FOLDER = "data/github/";

    public void ingestDocuments(
            String repoName
    ) {
        final String repoPath = REPO_FOLDER + repoName;
        final String filePath = repoPath + "/README.md";

        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            final var fileContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            final var file = new File(filePath);

            var document = buildDocumentFromResource(repoName, repoPath, file, fileContent);

            var ingestor = EmbeddingStoreIngestor
                    .builder()
                    .documentSplitter(recursive(200, 0))
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .build();

            ingestor.ingest(document);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static Document buildDocumentFromResource(String repo, String repoPath, File file, String fileContent) throws IOException {
        return new Document(
                fileContent,
                Metadata.from(
                        Map.of(
                                "repository",
                                repo,
                                "fileType",
                                FilenameUtils.getExtension(file.getName()),
                                "name",
                                file.getName(),
                                "path",
                                file.toPath().relativize(Path.of(repoPath)).toString(),
                                "parent",
                                file.getParent()
                        )
                )
        );
    }

}
