package dev.sashacorp.javarag.data;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jline.terminal.Terminal;
import org.springframework.stereotype.Service;

@Service
public record GitService(Terminal terminal) {
    public boolean cloneRepo(String repoName) {
        String repoUrl = "https://github.com/" + repoName + ".git";
        String cloneDirectoryPath = "data/github/" + repoName;
        Path path = Paths.get(cloneDirectoryPath);

        if (path.toFile().exists()) path.toFile().delete();

        try {
            Git.cloneRepository()
               .setURI(repoUrl)
               .setDirectory(path.toFile())
               .call();
            return true;
        } catch (GitAPIException e) {
            terminal.writer().println(" ⚠️  Exception occurred while cloning repo '" + repoUrl + "'");
            return false;
        }
    }
}
