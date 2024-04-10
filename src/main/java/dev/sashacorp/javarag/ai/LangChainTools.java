package dev.sashacorp.javarag.ai;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.langchain4j.agent.tool.Tool;

public record LangChainTools() {
    public static final String GENERATED_FILES_FOLDER = "generated/";
    @Tool
    void generateFile(String fileName, String fileContent) {
        Path path = Paths.get(GENERATED_FILES_FOLDER + fileName);
        byte[] strToBytes = fileContent.getBytes();

        try {
            Files.write(path, strToBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
