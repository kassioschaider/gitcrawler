package br.com.kassioschaider.gitcrawler.service.impl;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;
import br.com.kassioschaider.gitcrawler.model.GitRepository;
import br.com.kassioschaider.gitcrawler.service.DataGitFileService;
import br.com.kassioschaider.gitcrawler.util.ExtractDataUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;

@Service
@AllArgsConstructor
public class DataGitFileServiceImpl implements DataGitFileService {

    private static final String NO_EXTENSION = "no extension";
    private static final String DOT = ".";
    private static final String BYTES = "Bytes";
    private static final String BYTE = "Byte";
    private static final String KB = "KB";
    private static final String MULTI_KB = "1024";

    private static final String PATTERN_TO_LINES_TYPE_LINE = "([0-9]+) (lines)";
    private static final String PATTERN_TO_BYTES_TYPE_LINE = "([0-9]+\\.?[0-9]*?) (Bytes|Byte|KB)";
    private static final String PATTERN_TO_TYPE_FILE_BY_URL = "\\.[a-zA-Z]+$";

    private final ExtractDataUtil edt = new ExtractDataUtil();

    @Override
    public DataGitFile getDataGitFileByUrl(URL url) throws IOException {
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
