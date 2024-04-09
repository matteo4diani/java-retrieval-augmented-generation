# Local Retrieval Augmented Generation in Java

Run this project:

Start Ollama in a separate terminal tab (detached mode not supported yet):
```sh
ollama run codellama
```

Start Qdrant:
```sh
docker compose up -d
```

Run the CLI application:
```sh
mvn clean install spring-boot:run -DskipTests
```

The last build step produces an executable jar. You can put the jar on your path and alias it to start this app as a shell command.