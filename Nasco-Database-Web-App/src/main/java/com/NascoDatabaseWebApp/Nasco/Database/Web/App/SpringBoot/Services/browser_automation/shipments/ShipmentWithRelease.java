package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.LoginCredentials;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.PdfRelease;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.util.Gatepass;
import org.apache.pdfbox.pdmodel.PDDocument;

import static com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.SeleniumHelper.getClerkInitials;

public abstract class ShipmentWithRelease extends Shipment implements PdfRelease {

    public void createShipment(Gatepass gatepass, LoginCredentials tc3Credentials, LoginCredentials tmCredentials) {
        String clerkInitials = getClerkInitials(tc3Credentials.getUsername());
        PDDocument file = readFile(gatepass.getFile());
        Release release = parseRelease(convertToText(file));

        loginTc3(tc3Credentials);
        loginTm(tmCredentials);
        submitToTm(gatepass);
        createNewShipment();
        fillTransportationFields(release, clerkInitials);
        fillRemarks(getRemarks(release, clerkInitials));
        setInventory();
        setReceiver(release);
        setReceiverAddress(release);
        clickCreateButton();
        navigateToLoadingRequestOrShippedItems();
        addItemsToShipment(release);
        saveShipment();
    }

    protected abstract String getRemarks(Release release, String clerkInitials);

    protected abstract Release parseRelease(String release);

    protected abstract void setReceiver(Release release);

    protected abstract void setReceiverAddress(Release release);

    protected abstract void addItemsToShipment(Release release);


}
