package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.LoginCredentials;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.util.Gatepass;
import org.springframework.web.multipart.MultipartFile;

import static com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.SeleniumHelper.getClerkInitials;

public abstract class ShipmentWithRelease extends Shipment {

    public void createShipment(Gatepass gatepass, LoginCredentials tc3Credentials, LoginCredentials tmCredentials) {
        String clerkInitials = getClerkInitials(tc3Credentials.getUsername());
        Release release = parseRelease(gatepass.getFile());
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

    protected abstract Release parseRelease(MultipartFile file);

    protected abstract void setReceiver(Release release);

    protected abstract void setReceiverAddress(Release release);

    protected abstract void addItemsToShipment(Release release);


}
