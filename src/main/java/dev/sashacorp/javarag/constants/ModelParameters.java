package dev.sashacorp.javarag.constants;

import io.qdrant.client.grpc.Collections;

public record ModelParameters() {
    public static final Collections.Distance DISTANCE = Collections.Distance.Cosine;
    public static final int DIMENSION = 384;
    public static final int QDRANT_MAX_RESULTS = 10;
    public static final double QDRANT_MIN_SCORE = 0.6;
    public static final int CHAT_MEMORY_MAX_MESSAGES = 5;
    public static final int CHUNK_MAX_CHARS = 500;
    public static final int CHUNK_MAX_OVERLAP = 0;
}
