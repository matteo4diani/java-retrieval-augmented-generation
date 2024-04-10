package dev.sashacorp.javarag.ai;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import dev.langchain4j.agent.tool.Tool;
import dev.sashacorp.javarag.constants.Folders;

public record LangChainTools() {
    @Tool
    void generateFile(String fileName, String fileContent) {
        Path path = Paths.get(Folders.GENERATED_FILES_FOLDER + fileName);
        byte[] strToBytes = fileContent.getBytes();

        try {
            Files.write(path, strToBytes, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
