package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.boscus;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.FileFormatException;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.ShipmentWithRelease;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.RegexHelper.collectMatches;

public class BoscusShipment extends ShipmentWithRelease {

    @Override
    protected String getRemarks(Release release, String clerkInitials) {
        return String.format(
                "%s\nPO#: %s\nSKU#: %s\nWeights are estimates only and as such are subject to review and revision as necessary.\n%s",
                release.getOrder(), ((BoscusRelease) release).getPo(), ((BoscusRelease) release).getSku(), clerkInitials
                );
    }

    /* TODO */
    @Override
    protected void navigateToLoadingRequestOrShippedItems() {

    }

    public Release parseRelease(String release) {

        return BoscusRelease.builder()
                .po(getPo(release))
                .sku(getSku(release))
                .receiver(getReceiver(release))
                .receiverAddress(getReceiverAddress(release))
                .items(getItems(release))
                .build();
    }

    private List<BoscusItem> getItems(String release) {
        List<String> sizes = getSizes(release);
        List<String> pieceCounts = getPieceCounts(release);
        List<String> quantities = getQuantities(release);

        Stream.of(pieceCounts, quantities).forEach(it -> {
            if (sizes.size() != it.size()) {
                throw new FileFormatException("Number of fields of returned items did not match!");
            }
        });

        return combineIdenticalItemQuantities(sizes, pieceCounts, quantities);

    }

    // Returns a List whose contents are all unique Boscus Items, with duplicated having their quantities added into a single item
    private List<BoscusItem> combineIdenticalItemQuantities(List<String> sizes, List<String> pieceCounts, List<String> quantities) {

        List<BoscusItem> items = new ArrayList<>();

        for (int i = 0; i < sizes.size(); i++) {
            items.add(BoscusItem.builder()
                    .pieceCount(sizes.get(i))
                    .quantity(pieceCounts.get(i))
                    .size(quantities.get(i))
                    .build());
        }

        for (int i = 0; i < items.size(); i++) {
            for (int j = i; j < items.size(); j++) {
                if (items.get(i).isDuplicate(items.get(j))) {
                    items.get(i).setQuantity(Integer.toString(Integer.parseInt(items.get(i).getQuantity()) + Integer.parseInt(items.get(j).getQuantity())));
                    items.remove(j--);
                }
            }
        }

        return items;
    }

    private List<String> getSizes(String release) {
        List<String> sizes = collectMatches(Pattern.compile("(?<=\\s)\\d[xX]\\d[xX]\\d{2}'(?:\\s*\\d{2,3}(?:[\\s\\-]\\d/\\d)?)?",Pattern.MULTILINE), release);
        for (int i = 0; i < sizes.size(); i++) {
            sizes.set(i, sizes.get(i).replace("'", ""));
            if (sizes.get(i).contains(" ")) {
                sizes.set(i, sizes.get(i).substring(0, 4) + sizes.get(i).substring(6,sizes.size()));
            }
        }

        return sizes;
    }

    private List<String> getPieceCounts(String release) {
        return collectMatches(Pattern.compile("(?<=BFPK)\\d{3}"), release);
    }

    private List<String> getQuantities(String release) {
        return collectMatches(Pattern.compile("^\\d{1,2}(?=\\sBFPK)", Pattern.MULTILINE), release);
    }

    private String getReceiver(String release) {
        List<String> lines = release.lines().collect(Collectors.toList());
        return lines.get(lines.indexOf("SHIP TO:") - 1);
    }

    private String getReceiverAddress(String release) {
        List<String> lines = release.lines().collect(Collectors.toList());
        StringBuilder receiverAddress = new StringBuilder();

        for (String line : lines.subList(lines.indexOf("SHIP TO:") + 1, lines.indexOf("Loading Date:"))) {
            receiverAddress.append(line).append("\n");
        }

        return receiverAddress.toString().trim();
    }

    private String getPo(String release) {
        Matcher m = Pattern.compile("^[A-Z]{4}\\d{8}$", Pattern.MULTILINE).matcher(release);
        if (m.find()) {
            return m.group();
        }
        throw new FileFormatException("PO Number could not be found!");
    }

    private String getSku(String release) {
        List<String> lines = release.lines().collect(Collectors.toList());
        StringBuilder sku = new StringBuilder();

        for (String line : lines.subList(1, lines.indexOf("Destination P.O.# Sent By"))) {
            sku.append(line).append(" ");
        }

        return sku.substring(0, sku.deleteCharAt(sku.length()).lastIndexOf(" "));
    }

    /* TODO */
    @Override
    protected void addItemsToShipment(Release release) {

    }

}
