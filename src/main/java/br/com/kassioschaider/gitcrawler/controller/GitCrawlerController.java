package br.com.kassioschaider.gitcrawler.controller;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;
import br.com.kassioschaider.gitcrawler.model.GitLink;
import br.com.kassioschaider.gitcrawler.model.GitRepository;
import br.com.kassioschaider.gitcrawler.model.GitType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class GitCrawlerController {

    private static final String FILTER_TO_DIRECTORY_TYPE_LINK = "aria-label=\"Directory\"";
    private static final String FILTER_TO_FILE_TYPE_LINK = "aria-label=\"File\"";
    private static final String FILTER_TO_LINK_BY_TAG_CSS = "#repo-content-pjax-container";

    @PostMapping("/counter")
    public List<DataGitFile> fileCounter(@RequestBody GitRepository gitRepository) {
        List<GitLink> outputs = new ArrayList();

        try {
            URL urlRepository = new URL(gitRepository.getLinkRepository());
            extractLinks(outputs, urlRepository, gitRepository);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        System.out.println(outputs);
        return gitRepository.getDataGitFiles();
    }

    private List<GitLink> extractLinks(List<GitLink> output, URL nextLink, GitRepository gitRepository) {
        BufferedReader in;

        try {
            InputStream urlObject = nextLink.openStream();
            in = new BufferedReader(new InputStreamReader(urlObject));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                GitLink gl;

                if(this.filterInput(inputLine, FILTER_TO_DIRECTORY_TYPE_LINK)) {
                    gl = new GitLink();
                    gl.setType(GitType.DIRECTORY);
                    output.add(gl);
                }

                if(this.filterInput(inputLine, FILTER_TO_FILE_TYPE_LINK)) {
                    gl = new GitLink();
                    gl.setType(GitType.FILE);
                    output.add(gl);
                }

                if(this.filterInput(inputLine, FILTER_TO_LINK_BY_TAG_CSS)) {
                    final GitLink gitLink = output.get(output.size() - 1);

                    final URL url = new URL(this.getUrlByLine(inputLine));
                    gitLink.setUrl(url);

                    if(gitLink.getType().equals(GitType.DIRECTORY)) {
                        gitLink.setLinks(
                                extractLinks(
                                        gitLink.getLinks(),
                                        gitLink.getUrl(),
                                        gitRepository)
                        );
                    }

                    if(gitLink.getType().equals(GitType.FILE)) {
                        gitRepository.getDataGitFiles().add(getDataGitFileByUrl(url, gitRepository));
                    }
                }
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    private Boolean filterInput(String line, String filter) {
        return line.contains(filter);
    }

    private String getUrlByLine(String line) {
        Pattern pattern = Pattern.compile("href=([\"'])(.*?)\\1");
        Matcher matcher = pattern.matcher(line);
        String baseUrl = "https://github.com";
        String result = "";

        if(matcher.find()) {
            result = matcher.group();
            result = result.replace("href=\"",baseUrl).replace("\"", "");
        }

        return result;
    }

    private DataGitFile getDataGitFileByUrl(URL url, GitRepository gitRepository) {

        System.out.println(url.getFile());

        return new DataGitFile("ew",1,1,4);
    }
}
