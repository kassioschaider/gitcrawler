package br.com.kassioschaider.gitcrawler.service.impl;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;
import br.com.kassioschaider.gitcrawler.model.GitRepository;
import br.com.kassioschaider.gitcrawler.util.ExtractDataUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;

public class DataGitFileTask implements Runnable {

    private static final String NO_EXTENSION = "no extension";
    private static final String BYTES = "Bytes";
    private static final String BYTE = "Byte";
    private static final String KB = "KB";
    private static final String ZERO = "0";
    private static final BigDecimal BIG_DECIMAL_MULTI_KB = new BigDecimal("1024");

    private static final String REGEX_TO_LINES_FROM_LINE = "([0-9]+) (lines)";
    private static final String REGEX_TO_BYTES_FROM_LINE = "([0-9]+\\.?[0-9]*?) ("+BYTES+"|"+BYTE+"|"+KB+")";
    private static final String REGEX_TO_TYPE_FROM_URL = "(\\.)([a-zA-Z]+$)";
    private static final int GROUP_EXTENSION = 2;
    private static final int GROUP_MEASURE = 2;
    private static final int GROUP_BYTES = 1;
    private static final int GROUP_LINES = 1;

    private final ExtractDataUtil util = new ExtractDataUtil();

    private final URL url;
    private final GitRepository gitRepository;

    public DataGitFileTask(URL url, GitRepository gitRepository) {
        this.url = url;
        this.gitRepository = gitRepository;
    }

    @Override
    public void run() {
        getDataGitFileByUrl(url);
    }

    public void getDataGitFileByUrl(URL url) {

        DataGitFile dataGitFile = new DataGitFile();
        dataGitFile.setExtension(extractExtensionFromUrl(url));

        try {
            InputStream urlObject = url.openStream();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(urlObject))) {
                String lineFile;

                while ((lineFile = in.readLine()) != null) {
                    searchAndExtractData(dataGitFile, lineFile);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gitRepository.addDataGitFile(dataGitFile);
    }

    private void searchAndExtractData(DataGitFile dataGitFile, String lineFile) {
        extractLinesFromLine(dataGitFile, lineFile);
        extractBytesFromLine(dataGitFile, lineFile);
    }

    private void extractLinesFromLine(DataGitFile dataGitFile, String lineFile) {
        final String lines = util.filterTextLineByPatternAndGroup(lineFile, REGEX_TO_LINES_FROM_LINE, GROUP_LINES);

        if (!lines.equals(ExtractDataUtil.STRING_EMPTY)) {
            dataGitFile.setLines(Integer.parseInt(lines));
        }
    }

    private void extractBytesFromLine(DataGitFile dataGitFile, String lineFile) {
        final String bytes = util.filterTextLineByPatternAndGroup(lineFile, REGEX_TO_BYTES_FROM_LINE, GROUP_BYTES);

        if (!bytes.equals(ExtractDataUtil.STRING_EMPTY)) {
            dataGitFile.setBytes(extractBytesFromLineByMeasure(lineFile, bytes));
        }
    }

    private BigDecimal extractBytesFromLineByMeasure(String lineFile, String bytes) {
        String measure = util.filterTextLineByPatternAndGroup(lineFile, REGEX_TO_BYTES_FROM_LINE, GROUP_MEASURE);

        if(measure.equals(KB)) return (new BigDecimal(bytes)).multiply(BIG_DECIMAL_MULTI_KB);
        if(measure.equals(BYTES) || measure.equals(BYTE)) return new BigDecimal(bytes);
        return new BigDecimal(ZERO);
    }

    private String extractExtensionFromUrl(URL url) {
        String extension = util.filterTextLineByPatternAndGroup(url.getPath(), REGEX_TO_TYPE_FROM_URL, GROUP_EXTENSION);

        if (!extension.equals(ExtractDataUtil.STRING_EMPTY)) {
            return extension;
        }
        return NO_EXTENSION;
    }

}