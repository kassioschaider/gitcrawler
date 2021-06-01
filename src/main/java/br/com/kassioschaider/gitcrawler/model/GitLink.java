package br.com.kassioschaider.gitcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class GitLink {

    private String type;
    private URL url;
    private List<GitLink> links = new ArrayList();
}
