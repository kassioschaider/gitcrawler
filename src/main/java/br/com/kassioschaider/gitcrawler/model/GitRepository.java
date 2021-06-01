package br.com.kassioschaider.gitcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class GitRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    private String linkRepository;
    private List<DataGitFile> dataGitFiles = new ArrayList();
}
