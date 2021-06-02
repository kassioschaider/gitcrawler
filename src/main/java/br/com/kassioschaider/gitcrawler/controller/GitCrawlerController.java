package br.com.kassioschaider.gitcrawler.controller;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;
import br.com.kassioschaider.gitcrawler.model.GitLink;
import br.com.kassioschaider.gitcrawler.model.GitRepository;
import br.com.kassioschaider.gitcrawler.model.GitType;
import br.com.kassioschaider.gitcrawler.service.DataGitFileService;
import br.com.kassioschaider.gitcrawler.util.ExtractDataUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@RestController
@AllArgsConstructor
public class GitCrawlerController {

    private static final String BASE_URL = "https://github.com";
    private static final String FILTER_TO_DIRECTORY_TYPE_LINE = "aria-label=\"Directory\"";
    private static final String FILTER_TO_FILE_TYPE_LINE = "aria-label=\"File\"";
    private static final String FILTER_TO_LINK_BY_TAG_CSS = "#repo-content-pjax-container";
    private static final String PATTERN_TO_URL_BY_HREF = "href=([\"'])(.*?)\\1";
    private final ExtractDataUtil edt = new ExtractDataUtil();

    @Autowired
    private final DataGitFileService dataGitFileService;

    @PostMapping("/counter")
    public Set<DataGitFile> fileCounter(@RequestBody GitRepository gitRepository) {
        Set<GitLink> outputs = new HashSet<>();

        try {
            URL urlRepository = new URL(gitRepository.getLinkRepository());
            extractLinks(outputs, urlRepository, gitRepository);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return gitRepository.getDataGitFiles();
    }

    @PostMapping("/tree")
    public Set<GitLink> tree(@RequestBody GitRepository gitRepository) {
        Set<GitLink> outputs = new HashSet<>();

        try {
            URL urlRepository = new URL(gitRepository.getLinkRepository());
            extractLinks(outputs, urlRepository, gitRepository);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return outputs;
    }

    private void extractLinks(Set<GitLink> outputs, URL nextLink, GitRepository gitRepository) {
        BufferedReader in;

        try {
            InputStream urlObject = nextLink.openStream();
            in = new BufferedReader(new InputStreamReader(urlObject));
            String inputLine;

            GitLink gl = new GitLink();

            while ((inputLine = in.readLine()) != null) {

                if(edt.filterTypeLine(inputLine, FILTER_TO_DIRECTORY_TYPE_LINE)) {
                    gl.setType(GitType.DIRECTORY);
                }

                if(edt.filterTypeLine(inputLine, FILTER_TO_FILE_TYPE_LINE)) {
                    gl.setType(GitType.FILE);
                }

                if(edt.filterTypeLine(inputLine, FILTER_TO_LINK_BY_TAG_CSS)) {
                    final URL url = new URL(
                            BASE_URL + edt.filterTextLineByPatternAndGroup(inputLine,
                                    PATTERN_TO_URL_BY_HREF, 2));
                    gl.setUrl(url);

                    if(gl.getType().equals(GitType.DIRECTORY)) {
                        extractLinks(gl.getLinks(), gl.getUrl(), gitRepository);
                    }

                    if(gl.getType().equals(GitType.FILE)) {
                        gitRepository.addDataGitFile(dataGitFileService.getDataGitFileByUrl(url));
                    }

                    outputs.add(gl);
                }
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
