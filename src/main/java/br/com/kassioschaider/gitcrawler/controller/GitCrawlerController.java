package br.com.kassioschaider.gitcrawler.controller;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;
import br.com.kassioschaider.gitcrawler.model.GitLink;
import br.com.kassioschaider.gitcrawler.model.GitRepository;
import br.com.kassioschaider.gitcrawler.model.GitType;
import br.com.kassioschaider.gitcrawler.util.ExtractDataUtil;
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

@RestController
public class GitCrawlerController {

    private static final String BASE_URL = "https://github.com";
    private static final String NO_EXTENSION = "no extension";
    private static final String DOT = ".";
    private static final String BYTES = "Bytes";
    private static final String BYTE = "Byte";
    private static final String KB = "KB";
    private static final String MULTI_KB = "1024";

    private static final String FILTER_TO_DIRECTORY_TYPE_LINE = "aria-label=\"Directory\"";
    private static final String FILTER_TO_FILE_TYPE_LINE = "aria-label=\"File\"";
    private static final String FILTER_TO_LINK_BY_TAG_CSS = "#repo-content-pjax-container";

    private static final String PATTERN_TO_LINES_TYPE_LINE = "([0-9]+) (lines)";
    private static final String PATTERN_TO_BYTES_TYPE_LINE = "([0-9]+\\.?[0-9]*?) (Bytes|Byte|KB)";
    private static final String PATTERN_TO_URL_BY_HREF = "href=([\"'])(.*?)\\1";
    private static final String PATTERN_TO_TYPE_FILE_BY_URL = "\\.[a-zA-Z]+$";

    private final ExtractDataUtil edt = new ExtractDataUtil();

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

    @PostMapping("/tree")
    public Set<GitLink> tree(@RequestBody GitRepository gitRepository) {
        Set<GitLink> outputs = new HashSet<>();

        try {
            URL urlRepository = new URL(gitRepository.getLinkRepository());
            extractLinks(outputs, urlRepository, gitRepository);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return outputs;
    }

    private void extractLinks(Set<GitLink> outputs, URL nextLink, GitRepository gitRepository) {
        BufferedReader in;

        try {
            InputStream urlObject = nextLink.openStream();
            in = new BufferedReader(new InputStreamReader(urlObject));
            String inputLine;

            GitLink gl = new GitLink();

            while ((inputLine = in.readLine()) != null) {

                if(edt.filterTypeLine(inputLine, FILTER_TO_DIRECTORY_TYPE_LINE)) {
                    gl.setType(GitType.DIRECTORY);
                }

                if(edt.filterTypeLine(inputLine, FILTER_TO_FILE_TYPE_LINE)) {
                    gl.setType(GitType.FILE);
                }

                if(edt.filterTypeLine(inputLine, FILTER_TO_LINK_BY_TAG_CSS)) {
                    final URL url = new URL(
                            BASE_URL + edt.filterTextLineByPatternAndGroup(inputLine,
                                    PATTERN_TO_URL_BY_HREF, 2));
                    gl.setUrl(url);

                    if(gl.getType().equals(GitType.DIRECTORY)) {
                        extractLinks(gl.getLinks(), gl.getUrl(), gitRepository);
                    }

                    if(gl.getType().equals(GitType.FILE)) {
                        gitRepository.addDataGitFile(getDataGitFileByUrl(url));
                    }

                    outputs.add(gl);
                }
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DataGitFile getDataGitFileByUrl(URL url) throws IOException {
        BufferedReader in;
        DataGitFile dgt = new DataGitFile();
        String extension = edt.filterTextLineByPattern(url.getPath(), PATTERN_TO_TYPE_FILE_BY_URL)
                .replace(DOT, ExtractDataUtil.STRING_EMPTY);

        if (extension.equals(ExtractDataUtil.STRING_EMPTY)) {
            dgt.setExtension(NO_EXTENSION);
        } else {
            dgt.setExtension(extension);
        }

        InputStream urlObject = url.openStream();
        in = new BufferedReader(new InputStreamReader(urlObject));
        String inputLine;

       System.out.println(url.getFile());

        while ((inputLine = in.readLine()) != null) {

            final String s = edt.filterTextLineByPatternAndGroup(inputLine, PATTERN_TO_LINES_TYPE_LINE, 1);
            if (!s.equals(ExtractDataUtil.STRING_EMPTY)) {
               System.out.println("Lines: " + s);
                dgt.setLines(Integer.parseInt(edt.filterTextLineByPatternAndGroup(inputLine,
                        PATTERN_TO_LINES_TYPE_LINE,
                        1)));
            }
            else {
                final String s1 = edt.filterTextLineByPatternAndGroup(inputLine, PATTERN_TO_BYTES_TYPE_LINE, 1);

                if (!s1.equals(ExtractDataUtil.STRING_EMPTY)) {
                    String s3 = edt.filterTextLineByPatternAndGroup(inputLine, PATTERN_TO_BYTES_TYPE_LINE,2);
                   System.out.println(s3);

                    if(s3.equals(BYTES) || s3.equals(BYTE)) {
                        final BigDecimal bytes = new BigDecimal(edt.filterTextLineByPatternAndGroup(inputLine,
                                PATTERN_TO_BYTES_TYPE_LINE,
                                1));
                       System.out.println(bytes);
                        dgt.setBytes(bytes);
                    }

                    if(s3.equals(KB)) {
                        BigDecimal bg = new BigDecimal(edt.filterTextLineByPatternAndGroup(inputLine,
                                PATTERN_TO_BYTES_TYPE_LINE,
                                1));
                        final BigDecimal multiplicand = new BigDecimal(MULTI_KB);
                       System.out.println("Multi: " + multiplicand);
                        final BigDecimal multiply = bg.multiply(multiplicand);
                       System.out.println("Result: " + multiply);
                        dgt.setBytes(multiply);
                    }
                }
            }
        }

        in.close();
        return dgt;
    }
}
