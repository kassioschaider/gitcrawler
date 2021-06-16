package br.com.kassioschaider.gitcrawler.service.impl;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;
import br.com.kassioschaider.gitcrawler.util.ExtractDataUtil;

import java.math.BigDecimal;

public class Bytes implements Extractor {

    private static final String BYTES = "Bytes";
    private static final String BYTE = "Byte";
    private static final String KB = "KB";
    private static final String ZERO = "0";
    private static final BigDecimal BIG_DECIMAL_MULTI_KB = new BigDecimal("1024");

    private static final String REGEX_TO_BYTES_FROM_LINE = "([0-9]+\\.?[0-9]*?) ("+BYTES+"|"+BYTE+"|"+KB+")";
    private static final int GROUP_MEASURE = 2;
    private static final int GROUP_BYTES = 1;

    private final ExtractDataUtil util = new ExtractDataUtil();


    @Override
    public void extract(DataGitFile dataGitFile, String line) {
        final String bytes = util.filterTextLineByPatternAndGroup(line, REGEX_TO_BYTES_FROM_LINE, GROUP_BYTES);

        if (!bytes.equals(ExtractDataUtil.STRING_EMPTY)) dataGitFile.setBytes(extractBytesFromLineByMeasure(line, bytes));
    }

    private BigDecimal extractBytesFromLineByMeasure(String lineFile, String bytes) {
        String measure = util.filterTextLineByPatternAndGroup(lineFile, REGEX_TO_BYTES_FROM_LINE, GROUP_MEASURE);

        if(measure.equals(KB)) return (new BigDecimal(bytes)).multiply(BIG_DECIMAL_MULTI_KB);
        if(measure.equals(BYTES) || measure.equals(BYTE)) return new BigDecimal(bytes);
        return new BigDecimal(ZERO);
    }
}
