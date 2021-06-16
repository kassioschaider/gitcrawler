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

        try {
            URL urlRepository = new URL(gitRepository.getLinkRepository());
            dataGitFiles = gitRepositoryService.extractGitData(urlRepository, gitRepository);
            Thread.sleep(FIVE_SEC);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().body("Invalid link repository!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(dataGitFiles);
    }
}
