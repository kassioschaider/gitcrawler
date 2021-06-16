package br.com.kassioschaider.gitcrawler.service.impl;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;
import br.com.kassioschaider.gitcrawler.util.ExtractDataUtil;

public class Lines implements Extractor {
    private static final String REGEX_TO_LINES_FROM_LINE = "([0-9]+) (lines)";
    private static final int GROUP_LINES = 1;

    private final ExtractDataUtil util = new ExtractDataUtil();

    @Override
    public void extract(DataGitFile dataGitFile, String line) {
        final String lines = util.filterTextLineByPatternAndGroup(line, REGEX_TO_LINES_FROM_LINE, GROUP_LINES);

        if (!lines.equals(ExtractDataUtil.STRING_EMPTY)) dataGitFile.setLines(Integer.parseInt(lines));
    }
}
