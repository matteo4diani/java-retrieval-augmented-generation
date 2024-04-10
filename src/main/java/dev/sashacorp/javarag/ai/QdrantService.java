package dev.sashacorp.javarag.ai;

import java.util.concurrent.ExecutionException;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;

public record QdrantService(
        QdrantClient qdrantClient
) {
    public void recreateCollection(String collectionName, Collections.VectorParams vectorParams) {
        try {
            if (qdrantClient.listCollectionsAsync().get().contains(collectionName)) {
                qdrantClient.recreateCollectionAsync(
                        collectionName,
                        vectorParams
                ).get();
            } else {
                qdrantClient.createCollectionAsync(
                        collectionName,
                        vectorParams
                ).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
