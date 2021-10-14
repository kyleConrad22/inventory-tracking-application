package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions.algoma;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.*;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions.Reception;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.Key;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.RegexHelper.collectMatches;
import static com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.StringHelper.printOutput;
import static java.util.Map.entry;
import static com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.SeleniumHelper.getClerkInitials;

public class AlgomaReception extends Reception implements PdfRelease {

    public void uploadReleases(MultipartFile[] files, LoginCredentials credentials) {
        List<AlgomaRelease> orders = new ArrayList<>();
        for (MultipartFile file : files) {
            String convertedFile = convertToText(readFile(file));

            printOutput(convertedFile);

            orders.add(parseRelease(convertedFile));

        }
        try {
            loginTc3(credentials);
            for (AlgomaRelease order : orders) {
                createReception(order, getClerkInitials(credentials.getUsername()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endSession();
        }
    }

    private AlgomaRelease parseRelease(String release) {
        System.out.println("\nParsing release...");
        List<AlgomaItem> algomaItems = new ArrayList<>();
        String railcar = getRailcar(release);
        String offloadDate = "";
        String bolNumber = getBolNumber(release);
        for (String page : getPages(release)) {
            System.out.println(page);

            String poNumber = getPoNumber(page);
            String receiver = getReceiver(page);
            String order = getOrder(poNumber, receiver);

            page = removeHeader(page);

            List<String> types = getTypes(page);
            List<String> weights = getWeights(page);
            List<String> thicknesses = getThicknesses(page);
            List<String> itemNumbers = getItemNumbers(page);
            List<String> heats = getHeats(page);
            List<String> marks = getMarks(page);
            List<String> diameters = getWidths(page);

            Stream.of(weights, thicknesses, itemNumbers, heats, marks, diameters).forEach(it -> {

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
                    .order(order)
                    .itemNumber(itemNumbers.get(i))
                    .width(diameters.get(i))
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
                System.out.println(m.group());
               if (m.group().charAt(m.group().length()-1) == '1') {
                   return Collections.singletonList(release);
               } else {
                   System.out.println(lines.indexOf(line));
                   pageIndexes.add(lines.indexOf(line));
               }
            }
        }
        if (!pageIndexes.isEmpty()) {
            pageIndexes.add(lines.size()-1);

            List<String> pages = new ArrayList<>();

            for (int i = 0; i < pageIndexes.size()-1; i++) {

                StringBuilder page = new StringBuilder();

                for (int j = pageIndexes.get(i); j < pageIndexes.get(i+1); j++) {

                    page.append(lines.get(j)).append("\n");
                }
                pages.add(page.toString());
            }
            System.out.println(pages.size());
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
        Matcher m = Pattern.compile("^2\\d{5,7}$", Pattern.MULTILINE).matcher(page);
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

    private String getReceiver(String page) {
        List<String> lines = page.lines().collect(Collectors.toList());

        StringBuilder result = new StringBuilder();

        int i = lines.indexOf("SHIP TO") + 1;
        while (!lines.get(i).contains("DATE/TIME")) {
            result.append(lines.get(i++)).append("\n");
        }

        return result.toString();
    }

    private List<String> getWidths(String page) {
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

    private String getOrder(String poNumber, String receiver) {
        if (receiver.contains("C/O NASCO")) {
            return "Esmark Steel Group - Midwest - 17985";
        }
        Map<String, String> hm = Map.ofEntries(
               entry( "2202902", "Wheatland Tube, LLC - 74629"),
                entry("2201532", "Heidtman Butler IN - 48365"),
                entry("2202010", "Esmark C/O Sun Steel - 14016"),
                entry("2203488", "Viking Materials Inc. C/O Feralloy Midwest - 57504"),
                entry("2202011", "Esmark C/O CSI University Park - 19843"),
                entry("2203067", "Heidtman East Chicago, IN - 41941"),
                entry("2203121", "Viking Materials Inc - Franklin Park - 56427"),
                entry("2000369", "JDM Steel Chicago Heights, IL - 65714"),
                entry("2203634", "Signode - 84261"),
                entry("20043", "ASI C/O Voss Clark - 20043"),
                entry("2203302", "Esmark C/O Ratner Steel - 12850"),
                entry("20018", "ASI C/O Heidtman Granite City - 45209"),
                entry("2203158", "Steel Warehouse - 93158"),
                entry("2000762", "Olympic Steel - Gary - 38367")
        );
        return hm.get(poNumber);
    }

    protected void createReception(Release release, String clerkInitials) {
        createNewAction("Iroquois Landing", "Railcar", "Breakbulk");
        fillTransportationFields(
            TransportationFields.builder()
                .carrier("CN")
                .driverName(clerkInitials)
                .carrierBill("Add In")
                .transportationNumber(((AlgomaRelease) release).getRailcar())
                .build()
        );
        fillRemarks(getRemarks(release, clerkInitials));
        setInventory("Algoma 2021");
        clickCreateButton();
        navigateToIncomingItems();
        createImportManifest(release);
        importManifest();
    }

    private String getRemarks(Release release, String clerkInitials) {
        HashMap<String, Integer> hm = new HashMap<>();

        for (AlgomaItem item : ((AlgomaRelease) release).getItems()) {
            String receiver = item.getReceiver();
            if (hm.containsKey(receiver)) {
                hm.put(receiver, hm.get(receiver) + 1);
            } else {
                hm.put(receiver, 1);
            }
        }

        StringBuilder remarks  = new StringBuilder();

        remarks.append(((AlgomaRelease) release).getRailcar())
                .append("\n")
                .append("Offloaded on ")
                .append(((AlgomaRelease) release).getOffloadDate());

        hm.forEach((receiver, count) -> {

            String coils = "\n\n%d Coil%s for:\n";
            if (count == 1) {
                remarks.append(String.format(coils, count, ""));
            } else {
                remarks.append(String.format(coils, count, "s"));
            }
            remarks.append(receiver);
        });

        remarks.append("\n").append(clerkInitials);

        return remarks.toString();
    }

    @Override
    protected void addReleaseItems(Sheet sheet, Release release) {
        System.out.printf("\nAdding items for release %s...\n", ((AlgomaRelease) release).getBolNumber());
        for (AlgomaItem item : ((AlgomaRelease) release).getItems()) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            row.createCell(0).setCellValue("Algoma 2021");
            row.createCell(1).setCellValue(item.getOrder());
            row.createCell(2).setCellValue("Steel Coils USA");
            row.createCell(3).setCellValue(item.getType());
            row.createCell(4).setCellValue("Loose");
            row.createCell(5).setCellValue("1");
            row.createCell(6).setCellValue("Can be containerized");
            row.createCell(8).setCellValue("1");
            row.createCell(9).setCellValue("Mark");
            row.createCell(10).setCellValue(item.getMark());
            row.createCell(11).setCellValue("HeatNumber");
            row.createCell(12).setCellValue(item.getHeat());
            row.createCell(13).setCellValue("Scope");
            row.createCell(14).setCellValue(((AlgomaRelease) release).getBolNumber() + " / " + item.getPoNumber() + " / " + item.getItemNumber());
            row.createCell(15).setCellValue("Other");
            row.createCell(16).setCellValue(((AlgomaRelease) release).getOffloadDate());
            row.createCell(17).setCellValue("Thickness");
            row.createCell(18).setCellValue(item.getThickness());
            row.createCell(19).setCellValue("in");
            row.createCell(20).setCellValue("Width");
            row.createCell(21).setCellValue(item.getWidth());
            row.createCell(22).setCellValue("in");
            row.createCell(31).setCellValue(item.getWeight());
            row.createCell(32).setCellValue("lb");
            row.createCell(33).setCellValue("Rail Building");
        }
    }

    protected void fillRemarks(String remarks) {
        System.out.println("\nFilling special instructions...");
        driver.findElement(By.id("specialInstructions")).sendKeys(remarks);
    }

}
