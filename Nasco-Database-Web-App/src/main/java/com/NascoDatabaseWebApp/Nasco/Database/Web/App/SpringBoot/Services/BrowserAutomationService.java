package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.LoginCredentials;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions.algoma.AlgomaReception;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.util.Gatepass;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.Shipment;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.util.ShipmentHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BrowserAutomationService {
    public void createAlgomaReception(MultipartFile[] inFiles, LoginCredentials tc3Credentials) {
        AlgomaReception algomaReception = new AlgomaReception();
        algomaReception.uploadReleases(inFiles, tc3Credentials);
    }

    public void createShipment(Gatepass gatepass, LoginCredentials tc3Credentials, LoginCredentials tmCredentials) {
        Shipment shipment = ShipmentHelper.getShipmentType(gatepass);
        shipment.createShipment(gatepass, tc3Credentials, tmCredentials);
    }
}
