package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.rusal;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.ShipmentWithRelease;
import org.springframework.web.multipart.MultipartFile;

public class RusalShipment extends ShipmentWithRelease {
    @Override
    protected void fillRemarks(String remarks) {

    }

    @Override
    protected void fillTransportationFields(Release release, String clerkInitials) {

    }

    @Override
    protected String getRemarks(Release release, String clerkInitials) {
        return null;
    }

    @Override
    public Release parseRelease(MultipartFile file) {
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
