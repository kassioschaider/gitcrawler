package br.com.kassioschaider.gitcrawler.util;

import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
public class ExtractDataUtil {

    public static final String STRING_EMPTY = "";

    public Boolean filterTypeLine(String line, String filter) {
        return line.contains(filter);
    }

    public String filterTextLineByPatternAndGroup(String line, String filter, int group) {
        Pattern pattern = Pattern.compile(filter);
        Matcher matcher = pattern.matcher(line);

        if(matcher.find()) {
            return matcher.group(group);
        }

        return STRING_EMPTY;
    }
}
