package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions.algoma;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.LoginCredentials;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.PdfRelease;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions.Reception;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.FileFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlgomaReception extends Reception implements PdfRelease {

    public void uploadReleases(MultipartFile[] files, LoginCredentials tc3Credentials) {
        List<AlgomaRelease> orders = new ArrayList<>();
        for (MultipartFile file : files) {
            String convertedFile = convertToText(readFile(file));

            orders.add(parseRelease(convertedFile));

            printOutput(convertedFile + orders.get(0));

        }
        loginTc3(tc3Credentials);
        for (AlgomaRelease order : orders) {
            createReception(order);
        }
        endSession();
    }

    private AlgomaRelease parseRelease(String release) {
        List<AlgomaItem> algomaItems = new ArrayList<>();
        String railcar = getRailcar(release);
        String offloadDate = "";
        String bolNumber = getBolNumber(release);
        for (String page : getPages(release)) {
            String poNumber = getPoNumber(page);

            page = removeHeader(page);

            String receiver = getReceiver(poNumber);
            List<String> types = getTypes(page);
            List<String> weights = getWeights(page);
            List<String> thicknesses = getThicknesses(page);
            List<String> itemNumbers = getItemNumbers(page);
            List<String> heats = getHeats(page);
            List<String> marks = getMarks(page);
            List<String> diameters = getDiameters(page);

            Stream.of(types, weights, thicknesses, itemNumbers, heats, marks, diameters).forEach(it -> {
                System.out.println(it.size());
                System.out.println(it.toString());
                if (types.size() != it.size()) {
                    throw new FileFormatException("Number of fields of returned items did not match!");
                }
            });

            for (int i = 0; i < types.size(); i++) {
                algomaItems.add(AlgomaItem.builder()
                    .type(types.get(i))
                    .weight(weights.get(i))
                    .thickness(thicknesses.get(i))
                    .receiver(receiver)
                    .mark(marks.get(i))
                    .poNumber(poNumber)
                    .itemNumber(itemNumbers.get(i))
                    .diameter(diameters.get(i))
                    .heat(heats.get(i))
                        .build());
            }

        }

        return AlgomaRelease.builder()
                .railcar(railcar)
                .items(algomaItems)
                .offloadDate(offloadDate)
                .bolNumber(bolNumber)
                    .build();
    }

    private String removeHeader(String page) {
        List<String> lines = page.lines().collect(Collectors.toList());

        StringBuilder result = new StringBuilder();
        for (int i = lines.indexOf("DESCRIPTION"); i < lines.size(); i++) {
            result.append(lines.get(i)).append("\n");
        }
        return result.toString();
    }

    private List<String> getPages(String release) {

        List<String> lines = release.lines().collect(Collectors.toList());
        List<Integer> pageIndexes = new ArrayList<>();

        for (String line : lines) {
            Matcher m = Pattern.compile("PAGE\\s\\d\\s/\\s\\d").matcher(line);
            if (m.find()) {
               if (m.group().charAt(m.group().length()-1) == '1') {
                   return Collections.singletonList(release);
               } else {
                   pageIndexes.add(lines.indexOf(line));
               }
            }
        }
        if (!pageIndexes.isEmpty()) {
            List<String> pages = new ArrayList<>();

            for (int i : pageIndexes.subList(0, pageIndexes.size()-2)) {
                StringBuilder page = new StringBuilder();

                for (int j = i; j < pageIndexes.get(i + 1); j++) {
                    page.append(lines.get(i)).append("\n");
                }
                pages.add(page.toString());
            }
            return pages;
        }

        throw new FileFormatException("File not of know format!");
    }

    private List<String> getWeights(String page) {
        return collectMatches(
            Pattern.compile("(?<=\\s)\\d{1,2},\\d{3}(?=\\s$)", Pattern.MULTILINE),
            page
        );
    }

    private List<String> getThicknesses(String page) {
        return collectMatches(
            Pattern.compile("(?<=:\\s)\\d\\.\\d{4}(?=\"\\s)", Pattern.MULTILINE),
            page
        );
    }

    private String getPoNumber(String page) {
        Matcher m = Pattern.compile("^2\\d{6}$", Pattern.MULTILINE).matcher(page);
        if (m.find()) {
            return m.group();
        }
        return "";
    }

    private String getRailcar(String release) {
        Matcher m = Pattern.compile("^[A-Z]+\\s?\\d+$", Pattern.MULTILINE).matcher(release);
        if (m.find()) {
            return m.group();
        }
        return "";
    }

    private String getBolNumber(String release) {
        Matcher m = Pattern.compile("^\\d{10}$", Pattern.MULTILINE).matcher(release);
        if (m.find()) {
            return m.group();
        }
        return "";
    }

    private String getReceiver(String poNumber) {
        Map<String, String> receiverMap = Map.of(
                "2202011", "ESMARK STEEL GROUP-MIDWEST, LLC\nC/O CHICAGO STEEL AND IRON LLC\n700 CENTRAL AVENUE\nUNIVERSITY PARK, IL\nUSA 60454",
                "2203648", "ESMARK STEEL GROUP-MIDWEST, LLC\nC/O NASCO\n9301 SOUTH KREITER AVE\nCHICAGO, IL\nUSA 60617"
        );

        return receiverMap.get(poNumber);
    }

    private List<String> getDiameters(String page) {
        return collectMatches(
            Pattern.compile("(?<=[xX]\\s)\\d{2}\\.\\d{3}(?=\"\\s)", Pattern.MULTILINE),
            page
        );
    }

    private List<String> getHeats(String page) {
        return collectMatches(
            Pattern.compile("^\\d{4}[A-Z]{1,2}\\d{1,2}\\s\\d{2}(?=\\s)", Pattern.MULTILINE),
            page
        );
    }

    private List<String> getMarks(String page) {
        return collectMatches(
            Pattern.compile("(?<=\\s)[A-Z]{3}[0-9]{4}[A-Z]?(?=\\s)", Pattern.MULTILINE),
            page
        );
    }

    private List<String> getItemNumbers(String page) {
        return collectMatches(
            Pattern.compile("(?<=^CUSTOMER\\sPO/ITEM\\sNO\\.:)\\S*", Pattern.MULTILINE),
            page
        );
    }

    private List<String> getTypes(String page) {
        Map<String, String> typeMap = Map.of(
                "CR STEEL SHEET", "Cold Rolled Steel Sheet",
                "HR STEEL SHEET", "Hot Rolled Steel Sheet",
                "HR FLOOR PLATE", "HR FLOOR PLATE",
                "HOT ROLLED COIL", "Hot Rolled Coils"
        );
        List<String> matches = new ArrayList<>();
        Matcher m = Pattern.compile("^[A-Z]+\\s[A-Z]+\\s[A-Z]+$", Pattern.MULTILINE).matcher(page);

        while (m.find()) {
            matches.add(typeMap.get(m.group()));
        }

        return matches;
    }

    private void printOutput(String str) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            writer.write(str);
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException("An exception occurred while attempting to write to file: " + e.getMessage());
        }
    }

    private List<String> collectMatches(Pattern pattern, String query) {
        List<String> matches = new ArrayList<>();
        Matcher m = pattern.matcher(query);

        while (m.find()) {
            matches.add(m.group());
        }

        return matches;
    }

    @Override
    protected void createReception(Release release) {

    }

    @Override
    protected XSSFWorkbook createImportManifest(Release release) {
        return null;
    }
}
