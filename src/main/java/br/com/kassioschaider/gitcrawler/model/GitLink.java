package br.com.kassioschaider.gitcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class GitLink {

    private GitType type;
    private URL url;
    private Set<GitLink> links = new HashSet<>();
}
