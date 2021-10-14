package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexHelper {

    public static List<String> collectMatches(Pattern pattern, String query) {
        List<String> matches = new ArrayList<>();
        Matcher m = pattern.matcher(query);

        while (m.find()) {
            matches.add(m.group());
        }

        return matches;
    }
}
