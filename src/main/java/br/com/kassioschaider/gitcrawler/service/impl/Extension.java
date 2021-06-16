package br.com.kassioschaider.gitcrawler.service.impl;

import br.com.kassioschaider.gitcrawler.model.DataGitFile;
import br.com.kassioschaider.gitcrawler.util.ExtractDataUtil;

public class Extension implements Extractor {

    private static final String NO_EXTENSION = "no extension";
    private static final String REGEX_TO_TYPE_FROM_URL = "(\\.)([a-zA-Z]+$)";
    private static final int GROUP_EXTENSION = 2;

    private final ExtractDataUtil util = new ExtractDataUtil();

    @Override
    public void extract(DataGitFile dataGitFile, String line) {
        String extension = util.filterTextLineByPatternAndGroup(line, REGEX_TO_TYPE_FROM_URL, GROUP_EXTENSION);
        dataGitFile.setExtension(getExtensionOrNoExtension(extension));
    }

    private String getExtensionOrNoExtension(String extension) {
        if (!extension.equals(ExtractDataUtil.STRING_EMPTY)) return extension;

        return NO_EXTENSION;
    }
}
