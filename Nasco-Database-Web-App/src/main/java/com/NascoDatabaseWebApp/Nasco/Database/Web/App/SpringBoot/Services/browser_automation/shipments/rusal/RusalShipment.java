package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.rusal;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.ShipmentWithRelease;

public class RusalShipment extends ShipmentWithRelease {
    @Override
    protected void fillRemarks(String remarks) {

    }

    @Override
    protected void fillTransportationFields(Release release, String clerkInitials) {

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
        return null;
    }

    @Override
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
