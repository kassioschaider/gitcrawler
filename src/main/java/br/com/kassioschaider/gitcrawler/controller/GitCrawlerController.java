package br.com.kassioschaider.gitcrawler.controller;

import br.com.kassioschaider.gitcrawler.model.GitLink;
import br.com.kassioschaider.gitcrawler.model.GitRepository;
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

    @PostMapping("/counter")
    public List<GitLink> fileCounter(@RequestBody GitRepository gitRepository) {
        List<GitLink> outputs = new ArrayList();
        URL repositoryTest = null;

        try {
            repositoryTest = new URL(gitRepository.getLinkRepository());
            extractLinks(outputs, repositoryTest);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return outputs;
    }

    private List<GitLink> extractLinks(List<GitLink> output, URL link) {
        BufferedReader in;

        try {
            InputStream urlObject = link.openStream();
            in = new BufferedReader(new InputStreamReader(urlObject));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                GitLink gl;

                if(this.filterInput(inputLine, "aria-label=\"Directory\"")) {
                    gl = new GitLink();
                    gl.setType("Directory");
                    output.add(gl);
                }

                if(this.filterInput(inputLine, "aria-label=\"File\"")) {
                    gl = new GitLink();
                    gl.setType("File");
                    output.add(gl);
                }

                if(this.filterInput(inputLine, "#repo-content-pjax-container")) {
                    final GitLink gitLink = output.get(output.size() - 1);

                    gitLink.setUrl(new URL(this.getUrlByLine(inputLine)));

                    if(gitLink.getType().equals("Directory")) {
                        gitLink.setLinks(
                                extractLinks(
                                        gitLink.getLinks(),
                                        gitLink.getUrl())
                        );
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
}
