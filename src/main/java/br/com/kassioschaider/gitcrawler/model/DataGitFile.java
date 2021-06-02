package br.com.kassioschaider.gitcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class DataGitFile {

    private String extension;
    private int count;
    private int lines;
    private BigDecimal bytes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataGitFile that = (DataGitFile) o;
        return extension.equals(that.extension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extension);
    }
}
