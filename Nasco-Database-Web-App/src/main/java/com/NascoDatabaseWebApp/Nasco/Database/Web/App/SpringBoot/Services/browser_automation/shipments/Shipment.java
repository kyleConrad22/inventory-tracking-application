package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.AutomatedSession;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.LoginCredentials;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.util.Gatepass;

public abstract class Shipment extends AutomatedSession {

    public abstract void createShipment(Gatepass gatepass, LoginCredentials tc3Credentials, LoginCredentials tmCredentials);

    protected void loginTm(LoginCredentials tmCredentials) {

    }

    protected void submitToTm(Gatepass gatepass) {

    }

    protected void createNewShipment() {

    }

    protected void clickCreateButton() {

    }

    protected abstract void setInventory();

    protected void navigateToLoadingRequestOrShippedItems() {

    }

    protected abstract void saveShipment();
}
