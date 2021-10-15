package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.boscus;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.ShipmentWithRelease;

public class BoscusShipment extends ShipmentWithRelease {

    public BoscusShipment() {
        addLoadingRequest = true;
    }

    @Override
    protected String getRemarks(Release release, String clerkInitials) {
        return String.format(
                "%s\nPO#: %s\nSKU#: %s\nWeights are estimates only and as such are subject to review and revision as necessary.\n%s",
                release.getOrder(), ((BoscusRelease) release).getPo(), ((BoscusRelease) release).getSku(), clerkInitials
                );
    }

    @Override
    public Release parseRelease(String release) {
        return new BoscusRelease(release);
    }

    /* TODO */
    @Override
    protected void addItemsToShipment(Release release) {

    }

}
