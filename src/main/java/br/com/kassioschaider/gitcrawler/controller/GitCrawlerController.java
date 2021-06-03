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
import java.util.Set;

@RestController
@AllArgsConstructor
public class GitCrawlerController {

    public static final int FIVE_SEC = 5000;
    @Autowired
    private final GitRepositoryService gitRepositoryService;

    @PostMapping("/counter")
    public ResponseEntity<Set<DataGitFile>> fileCounter(@RequestBody GitRepository gitRepository) {
        Set<DataGitFile> dataGitFiles = null;

        try {
            URL urlRepository = new URL(gitRepository.getLinkRepository());
            dataGitFiles = gitRepositoryService.extractGitData(urlRepository, gitRepository);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(FIVE_SEC);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(dataGitFiles);
    }
}
