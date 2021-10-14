package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.boscus.BoscusRelease;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.LoginCredentials;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.PdfRelease;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.util.Gatepass;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.TransportationFields;
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
        fillTransportationFields(
            TransportationFields.builder()
                .carrier(gatepass.getTruckingCompany())
                .driverName(gatepass.getDriverName())
                .carrierBill(gatepass.getDor())
                .transportationNumber(gatepass.getLicense())
                .build()
        );
        fillRemarks(getRemarks(release, clerkInitials));
        setReceiver(((BoscusRelease) release).getReceiver());
        setReceiverAddress(((BoscusRelease) release).getReceiverAddress());
        clickCreateButton();
        navigateToLoadingRequestOrShippedItems();
        addItemsToShipment(release);
        saveShipment();
    }

    protected abstract Release parseRelease(String release);

}
