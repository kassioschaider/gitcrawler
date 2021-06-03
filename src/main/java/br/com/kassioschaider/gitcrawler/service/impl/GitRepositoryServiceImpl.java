package br.com.kassioschaider.gitcrawler.service.impl;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;
import br.com.kassioschaider.gitcrawler.model.GitLink;
import br.com.kassioschaider.gitcrawler.model.GitRepository;
import br.com.kassioschaider.gitcrawler.model.GitType;
import br.com.kassioschaider.gitcrawler.service.DataGitFileService;
import br.com.kassioschaider.gitcrawler.service.GitRepositoryService;
import br.com.kassioschaider.gitcrawler.util.ExtractDataUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;

@Service
@AllArgsConstructor
public class GitRepositoryServiceImpl implements GitRepositoryService {

    private static final String BASE_URL = "https://github.com";
    private static final String FILTER_TO_DIRECTORY_TYPE_LINE = "aria-label=\"Directory\"";
    private static final String FILTER_TO_FILE_TYPE_LINE = "aria-label=\"File\"";
    private static final String FILTER_TO_LINK_BY_TAG_CSS = "#repo-content-pjax-container";
    private static final String REGEX_TO_URL_FROM_HREF_LINE = "href=([\"'])(.*?)\\1";
    private final ExtractDataUtil util = new ExtractDataUtil();

    @Autowired
    private final DataGitFileService dataGitFileService;

    @Override
    public Set<DataGitFile> extractGitData(URL nextLink, GitRepository gitRepository) {
        BufferedReader in;

        try {
            InputStream urlObject = nextLink.openStream();
            in = new BufferedReader(new InputStreamReader(urlObject));
            String inputLine;

            GitLink gitLink = new GitLink();

            while ((inputLine = in.readLine()) != null) {

                if(util.filterTypeLine(inputLine, FILTER_TO_DIRECTORY_TYPE_LINE)) {
                    gitLink.setType(GitType.DIRECTORY);
                }

                if(util.filterTypeLine(inputLine, FILTER_TO_FILE_TYPE_LINE)) {
                    gitLink.setType(GitType.FILE);
                }

                if(util.filterTypeLine(inputLine, FILTER_TO_LINK_BY_TAG_CSS)) {
                    final URL url = new URL(
                            BASE_URL + util.filterTextLineByPatternAndGroup(inputLine,
                                    REGEX_TO_URL_FROM_HREF_LINE, 2));
                    gitLink.setUrl(url);

                    if(gitLink.getType().equals(GitType.DIRECTORY)) {
                        extractGitData(gitLink.getUrl(), gitRepository);
                    }

                    if(gitLink.getType().equals(GitType.FILE)) {
                        gitRepository.addDataGitFile(dataGitFileService.getDataGitFileByUrl(url));
                    }
                }
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gitRepository.getDataGitFiles();
    }
}
