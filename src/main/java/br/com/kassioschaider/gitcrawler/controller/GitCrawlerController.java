package br.com.kassioschaider.gitcrawler.controller;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;
import br.com.kassioschaider.gitcrawler.model.GitRepository;
import br.com.kassioschaider.gitcrawler.service.GitRepositoryService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Set;

@RestController
@AllArgsConstructor
public class GitCrawlerController {

    public static final int FIVE_SEC = 5000;
    @Autowired
    private final GitRepositoryService gitRepositoryService;

    @PostMapping("/counter")
    public ResponseEntity<?> fileCounter(@RequestBody GitRepository gitRepository) {
        Set<DataGitFile> dataGitFiles;
        URL urlRepository;

        try {
            urlRepository = new URL(gitRepository.getLinkRepository());
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body("Invalid link repository! Malformed URL!");
        }

        try {
            dataGitFiles = gitRepositoryService.extractGitData(urlRepository, gitRepository);
            Thread.sleep(FIVE_SEC);
        } catch (UnknownHostException e) {
            return ResponseEntity.badRequest().body("Invalid link repository! Unknown Host!");
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(dataGitFiles);
    }
}
