package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.boscus.BoscusRelease;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.*;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.util.Gatepass;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.enums.CargoType;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.enums.Site;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.enums.TransportationType;
import org.apache.pdfbox.pdmodel.PDDocument;

public abstract class ShipmentWithRelease extends Shipment implements PdfRelease {

    public void createShipment(Gatepass gatepass, LoginCredentials tc3Credentials, LoginCredentials tmCredentials) {
        String clerkInitials = getClerkInitials(tc3Credentials.getUsername());
        PDDocument file = readFile(gatepass.getFile());
        Release release = parseRelease(convertToText(file));

        loginTc3(tc3Credentials);
        loginTm(tmCredentials);
        submitToTm(gatepass);
        createNewAction(Site.IROQUOIS_LANDING, TransportationType.TRUCK, CargoType.BREAK_BULK);
        fillTransportationFields(
            TransportationFields.builder()
                .carrier(gatepass.getTruckingCompany())
                .driverName(gatepass.getDriverName())
                .carrierBill(gatepass.getDor())
                .transportationNumber(gatepass.getLicense())
                .build()
        );
        fillRemarks(getRemarks(release, clerkInitials));
        clickAddDestination();
        setReceiver(((BoscusRelease) release).getReceiver());
        setReceiverAddress(((BoscusRelease) release).getReceiverAddress());
        clickCreateButton();
        navigateToLoadingRequestOrShippedItems();
        addItemsToShipment(release);
    }

    protected abstract Release parseRelease(String release);

}
