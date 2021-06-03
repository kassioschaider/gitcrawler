package br.com.kassioschaider.gitcrawler.service;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;

import java.io.IOException;
import java.net.URL;

public interface DataGitFileService {

    DataGitFile getDataGitFileByUrl(URL url) throws IOException;
}
