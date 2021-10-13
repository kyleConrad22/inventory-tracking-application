package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.boscus;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.FileFormatException;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.PdfRelease;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.ShipmentWithRelease;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BoscusShipment extends ShipmentWithRelease {

    @Override
    protected void fillRemarks(String remarks) {

    }

    @Override
    protected void fillTransportationFields(Release release, String clerkInitials) {

    }

    @Override
    protected String getRemarks(Release release, String clerkInitials) {
        return String.format(
                "%s\nPO#: %s\nSKU#: %s\nWeights are estimates only and as such are subject to review and revision as necessary.\n%s",
                release.getOrder(), ((BoscusRelease) release).getPo(), ((BoscusRelease) release).getSku(), clerkInitials
                );
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
                throw new FileFormatException("umber of fields of returned items did not match!");
            }
        });
        List<BoscusItem> items = new ArrayList<>();
        for (int i = 0; i < sizes.size(); i++) {
            items.add(BoscusItem.builder()
                    .pieceCount(pieceCounts.get(i))
                    .quantity(quantities.get(i))
                    .size(sizes.get(i))
                    .build());
        }
        return items;
    }

    private List<String> getSizes(String release) {
        return null;
    }

    private List<String> getPieceCounts(String release) {
        return null;
    }

    private List<String> getQuantities(String release) {
        return null;
    }

    private String getReceiver(String release) {
        return null;
    }

    private String getReceiverAddress(String release) {
        return null;
    }

    private String getPo(String release) {
        return null;
    }

    private String getSku(String release) {
        return null;
    }

    protected void setReceiver(Release release) {

    }

    @Override
    protected void setReceiverAddress(Release release) {

    }

    @Override
    protected void addItemsToShipment(Release release) {

    }

    @Override
    protected void setInventory() {

    }

    @Override
    protected void saveShipment() {

    }
}
