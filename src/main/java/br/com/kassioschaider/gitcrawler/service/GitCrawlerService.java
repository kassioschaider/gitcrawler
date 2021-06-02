package br.com.kassioschaider.gitcrawler.service;

import br.com.kassioschaider.gitcrawler.model.GitLink;
import br.com.kassioschaider.gitcrawler.model.GitRepository;

import java.net.URL;
import java.util.Set;

public interface GitCrawlerService {

    Set<GitLink> extractGitData(Set<GitLink> output, URL nextLink, GitRepository gitRepository);

    Set<GitLink> extractTreeLinks(Set<GitLink> output, URL nextLink, GitRepository gitRepository);
}
