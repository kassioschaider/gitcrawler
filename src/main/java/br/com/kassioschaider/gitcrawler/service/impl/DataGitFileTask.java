package br.com.kassioschaider.gitcrawler.service.impl;

import br.com.kassioschaider.gitcrawler.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class DataGitFileTask implements Runnable {

    private final URL url;
    private final GitRepository gitRepository;

    public DataGitFileTask(URL url, GitRepository gitRepository) {
        this.url = url;
        this.gitRepository = gitRepository;
    }

    @Override
    public void run() {
        getDataGitFileByUrl(url);
    }

    public void getDataGitFileByUrl(URL url) {

        DataGitFile dataGitFile = new DataGitFile();
        extractDataFromLine(dataGitFile, url.getPath(), new Extension());

        try {
            InputStream urlObject = url.openStream();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(urlObject))) {
                String lineFile;

                while ((lineFile = in.readLine()) != null) {
                    searchAndExtractData(dataGitFile, lineFile);
                }

                urlObject.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gitRepository.addDataGitFile(dataGitFile);
    }

    private void searchAndExtractData(DataGitFile dataGitFile, String lineFile) {
        extractDataFromLine(dataGitFile, lineFile, new Lines());
        extractDataFromLine(dataGitFile, lineFile, new Bytes());
    }

    private void extractDataFromLine(DataGitFile dataGitFile, String lineFile, Extractor extractor) {
        extractor.extract(dataGitFile, lineFile);
    }

}
