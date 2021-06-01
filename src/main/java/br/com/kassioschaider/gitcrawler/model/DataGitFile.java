package br.com.kassioschaider.gitcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class DataGitFile {

    private String extension;
    private Integer count;
    private Integer lines;
    private Integer bytes;
}
