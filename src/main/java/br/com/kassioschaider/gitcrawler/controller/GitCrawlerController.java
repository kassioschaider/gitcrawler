package br.com.kassioschaider.gitcrawler.controller;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;
import br.com.kassioschaider.gitcrawler.model.GitLink;
import br.com.kassioschaider.gitcrawler.model.GitRepository;
import br.com.kassioschaider.gitcrawler.model.GitType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class GitCrawlerController {

    private static final String BASE_URL = "https://github.com";
    private static final String NO_EXTENSION = "no extension";
    private static final String STRING_EMPTY = "";
    private static final String DOT = ".";

    private static final String FILTER_TO_DIRECTORY_TYPE_LINE = "aria-label=\"Directory\"";
    private static final String FILTER_TO_FILE_TYPE_LINE = "aria-label=\"File\"";
    private static final String FILTER_TO_LINK_BY_TAG_CSS = "#repo-content-pjax-container";
    private static final String FILTER_TO_END_OF_MAIN = "</main>";

    private static final String PATTERN_TO_LINES_TYPE_LINE = "([0-9]+) (lines)";
    private static final String PATTERN_TO_BYTES_TYPE_LINE = "([0-9]+\\.?[0-9]*?) (Bytes|Byte|KB)";
    private static final String PATTERN_TO_URL_BY_HREF = "href=([\"'])(.*?)\\1";
    private static final String PATTERN_TO_TYPE_FILE_BY_URL = "\\.[a-zA-Z]+$";

    @PostMapping("/counter")
    public Set<DataGitFile> fileCounter(@RequestBody GitRepository gitRepository) {
        Set<GitLink> outputs = new HashSet<>();

        try {
            URL urlRepository = new URL(gitRepository.getLinkRepository());
            extractLinks(outputs, urlRepository, gitRepository);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return gitRepository.getDataGitFiles();
    }

    private Set<GitLink> extractLinks(Set<GitLink> output, URL nextLink, GitRepository gitRepository) {
        BufferedReader in;

        try {
            InputStream urlObject = nextLink.openStream();
            in = new BufferedReader(new InputStreamReader(urlObject));
            String inputLine;

            GitLink gl = new GitLink();

            while ((inputLine = in.readLine()) != null) {

                if(this.filterTypeLine(inputLine, FILTER_TO_DIRECTORY_TYPE_LINE)) {
                    gl = new GitLink();
                    gl.setType(GitType.DIRECTORY);
                }

                if(this.filterTypeLine(inputLine, FILTER_TO_FILE_TYPE_LINE)) {
                    gl = new GitLink();
                    gl.setType(GitType.FILE);
                }

                if(this.filterTypeLine(inputLine, FILTER_TO_LINK_BY_TAG_CSS)) {
                    final URL url = new URL(
                            BASE_URL + filterTextLineByPatternAndGroup(inputLine,
                                    PATTERN_TO_URL_BY_HREF, 2));
                    gl.setUrl(url);

                    if(gl.getType().equals(GitType.DIRECTORY)) {
                        //gl.setLinks(
                                extractLinks(
                                        gl.getLinks(),
                                        gl.getUrl(),
                                        gitRepository);
                        //);
                    }

                    if(gl.getType().equals(GitType.FILE)) {
                        getDataGitFileByUrl(url, gitRepository);
                    }

                    output.add(gl);
                }
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    private Boolean filterTypeLine(String line, String filter) {
        return line.contains(filter);
    }

    private String filterTextLineByPattern(String line, String filter) {
        Pattern pattern = Pattern.compile(filter);
        Matcher matcher = pattern.matcher(line);

        if(matcher.find()) {
            return matcher.group();
        }

        return STRING_EMPTY;
    }

    private String filterTextLineByPatternAndGroup(String line, String filter, int group) {
        Pattern pattern = Pattern.compile(filter);
        Matcher matcher = pattern.matcher(line);

        if(matcher.find()) {
            return matcher.group(group);
        }

        return STRING_EMPTY;
    }

    private void getDataGitFileByUrl(URL url, GitRepository gitRepository) throws IOException {
        BufferedReader in;
        DataGitFile dgt = new DataGitFile();
        String extension = filterTextLineByPattern(url.getPath(), PATTERN_TO_TYPE_FILE_BY_URL)
                .replace(DOT, STRING_EMPTY);

        if (extension.equals(STRING_EMPTY)) {
            dgt.setExtension(NO_EXTENSION);
        } else {
            dgt.setExtension(extension);
        }

        InputStream urlObject = url.openStream();
        in = new BufferedReader(new InputStreamReader(urlObject));
        String inputLine;

        System.out.println(url.getFile());

        while ((inputLine = in.readLine()) != null) {

            final String s = filterTextLineByPatternAndGroup(inputLine, PATTERN_TO_LINES_TYPE_LINE, 1);
            if (!s.equals(STRING_EMPTY)) {
                System.out.println("Lines: " + s);
                dgt.setLines(Integer.parseInt(filterTextLineByPatternAndGroup(inputLine,
                        PATTERN_TO_LINES_TYPE_LINE,
                        1)));
            }
            else {
                final String s1 = filterTextLineByPatternAndGroup(inputLine, PATTERN_TO_BYTES_TYPE_LINE, 1);

                if (!s1.equals(STRING_EMPTY)) {
                    String s3 = filterTextLineByPatternAndGroup(inputLine, PATTERN_TO_BYTES_TYPE_LINE,2);
                    System.out.println(s3);

                    if(s3.equals("Bytes") || s3.equals("Byte")) {
                        final BigDecimal bytes = new BigDecimal(filterTextLineByPatternAndGroup(inputLine,
                                PATTERN_TO_BYTES_TYPE_LINE,
                                1));
                        System.out.println(bytes);
                        dgt.setBytes(bytes);
                    }

                    if(s3.equals("KB")) {
                        BigDecimal bg = new BigDecimal(filterTextLineByPatternAndGroup(inputLine,
                                PATTERN_TO_BYTES_TYPE_LINE,
                                1));
                        final BigDecimal multiplicand = new BigDecimal("1024");
                        System.out.println("Multi: " + multiplicand);
                        final BigDecimal multiply = bg.multiply(multiplicand);
                        System.out.println("Result: " + multiply);
                        dgt.setBytes(multiply);
                    }

                    gitRepository.addDataGitFile(dgt);
                }
            }
        }

        in.close();
    }
}
