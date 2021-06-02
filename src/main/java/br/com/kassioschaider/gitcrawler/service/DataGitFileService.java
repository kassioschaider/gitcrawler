package br.com.kassioschaider.gitcrawler.service;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;
import br.com.kassioschaider.gitcrawler.model.GitRepository;

import java.io.IOException;
import java.net.URL;

public interface DataGitFileService {

    DataGitFile getDataGitFileByUrl(URL url) throws IOException;
}
