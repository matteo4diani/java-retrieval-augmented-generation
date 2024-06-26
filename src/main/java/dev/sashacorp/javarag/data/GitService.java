package dev.sashacorp.javarag.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.sashacorp.javarag.constants.Folders;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jline.terminal.Terminal;
import org.springframework.stereotype.Service;

@Service
public record GitService(Terminal terminal) {
    public boolean cloneRepo(String repoName) {
        String repoUrl = buildGitHubUrl(repoName);
        String cloneDirectoryPath = Folders.REPO_FOLDER + repoName;
        Path path = Paths.get(cloneDirectoryPath);

        try {
            if (path.toFile().exists()) {
                FileUtils.deleteDirectory(path.toFile());
            }

            Git.cloneRepository()
               .setURI(repoUrl)
               .setDirectory(path.toFile())
               .call();
            return true;
        } catch (IOException | GitAPIException e) {
            terminal.writer().println(" ⚠️  Exception occurred while cloning repo '" + repoUrl + "'");
            return false;
        }
    }

    private String buildGitHubUrl(String repoName) {
        return "https://github.com/" + repoName + ".git";
    }
}
