package br.com.kassioschaider.gitcrawler.service.impl;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;

public interface Extractor {

    void extract(DataGitFile dataGitFile, String line);
}
