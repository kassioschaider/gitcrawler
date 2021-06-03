package br.com.kassioschaider.gitcrawler.service;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;
import br.com.kassioschaider.gitcrawler.model.GitRepository;

import java.net.URL;
import java.util.Set;

public interface GitRepositoryService {

    Set<DataGitFile> extractGitData(URL nextLink, GitRepository gitRepository) throws InterruptedException;
}
