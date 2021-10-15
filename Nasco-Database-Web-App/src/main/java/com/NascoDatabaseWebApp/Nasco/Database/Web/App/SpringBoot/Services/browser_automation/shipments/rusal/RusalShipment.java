package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.rusal;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.ShipmentWithRelease;

public class RusalShipment extends ShipmentWithRelease {

    public RusalShipment() {
        addLoadingRequest = true;
    }

    @Override
    protected String getRemarks(Release release, String clerkInitials) {
        return String.format(
                "%s\nPO #: %s\nMaterial must be free of dirt and debris.\nWood runners must be sturdy.\n%s",
                release.getOrder(), ((RusalRelease) release).getPo(), clerkInitials
        );
    }

    @Override
    protected Release parseRelease(String release) {
        return new RusalRelease(release);
    }

    /* TODO */
    @Override
    protected void addItemsToShipment(Release release) {

    }

}
