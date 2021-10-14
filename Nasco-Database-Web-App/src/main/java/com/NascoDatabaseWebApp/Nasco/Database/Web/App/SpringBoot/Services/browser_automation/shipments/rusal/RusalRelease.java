package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.rusal;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.FileFormatException;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.Release;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RusalRelease extends Release {
    private List<RusalItem> items;
    private String po;
    private RusalCommodity commodity;
    private String receiver;
    private String receiverAddress;

    public RusalRelease(String release) {
        commodity = parseCommodity(release);
        po = parsePo(release, parseBl(release));
        receiver = parseReceiver(release);
        receiverAddress = parseReceiverAddress(release, receiver);
        items = parseItems(release, commodity);
    }

    // Currently only gets a single Rusal item, need releases which contain multiple items to be able to add logic for multiple items
    private static List<RusalItem> parseItems(String release, RusalCommodity commodity) {
        List<RusalItem> items = new ArrayList<>();
        String bl = parseBl(release);

        items.add(
                RusalItem.builder()
                        .bl(bl)
                        .pieceCount(parsePieceCount(release, commodity))
                        .quantity(parseQuantity(release, commodity))
                        .build()
        );
        return items;
    }

    private static String parseBl(String release) {
        Matcher m = Pattern.compile("(?<=logs\\s|\\(Sr\\)\\s)\\w+\\R(?:\\d{2})?").matcher(release);
        if (m.find()) {
            return m.group();
        }
        throw new FileFormatException("BL could not be found!");
    }

    private static RusalCommodity parseCommodity(String release) {
        if (release.contains("INGOTS")) {
            return RusalCommodity.INGOTS;
        }
        return RusalCommodity.BILLETS;
    }

    private static String parseQuantity(String release, RusalCommodity commodity) {
        Matcher m;

        if (commodity == RusalCommodity.INGOTS) {
            m = Pattern.compile("(?<=\\s\\()\\s{2}(?=\\sBUNDLES\\)\\s)").matcher(release);
        } else {
            m = Pattern.compile("(?<=\\s\\()\\d{1,2}(?=\\sPIECES/TRUCK\\)\\s)").matcher(release.replace("PC", "PIECE").replace("TK", "TRUCK"));
        }
        if (m.find()) {
            return m.group();
        }
        throw new FileFormatException("Could not find quantity!");
    }


    private static String parsePo(String release, String bl) {
        Matcher m = Pattern.compile("^.+/\\sS-\\d+\\s/\\s[A-Z]{3}'\\d{2}(?=\\s)", Pattern.MULTILINE).matcher(release);
        if (m.find()) {
            String match = m.group();
            if (match.substring(0, 2).equals(bl.substring(bl.length()-2))) {
                match = match.substring(2);
            }
            return match;
        }
        throw new FileFormatException("Could not find PO number!");
    }

    private static String parsePieceCount(String release, RusalCommodity commodity) {
        if (commodity == RusalCommodity.BILLETS) {
            Matcher m = Pattern.compile("(?<=\\s|^)\\d{1,2}(?=pc/bun)(?=\\s|$)", Pattern.MULTILINE).matcher(release);
            if (m.find()) {
                return m.group();
            }
            throw new FileFormatException("Could not find piece count for billet load!");
        }
        return "N/A";
    }

    private static String parseReceiver(String release) {
        Matcher m = Pattern.compile("(?<=DELIVERY\\sTO:\\s).+(?:LLC|Inc\\.?|INC\\.?|$)", Pattern.MULTILINE).matcher(release);
        if (m.find()) {
            return m.group();
        }
        throw new FileFormatException("Could not find receiver!");
    }

    private static String parseReceiverAddress(String release, String receiver) {
        List<String> lines = release.lines().collect(Collectors.toList());
        StringBuilder address = new StringBuilder();
        try {
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).contains(receiver)) {
                    for (int j = i; j < lines.size(); j++) {
                        address.append(lines.get(j)).append("\n");
                    }
                    return (address.toString().trim());
                }
            }
            throw new FileFormatException("Could not find address!");

        } catch (Exception e) {
            throw new FileFormatException("An exception occurred while attempting to find receiver address: " + e.getMessage());
        }
    }
}
