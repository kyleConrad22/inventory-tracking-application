package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Controller;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.BrowserAutomationService;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.LoginCredentials;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.util.Gatepass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Controller
@RequestMapping("/api/browser")
public class BrowserAutomationController {

    @Autowired
    private BrowserAutomationService browserAutomationService;

    @PostMapping("/reception/algoma")
    @ResponseStatus(HttpStatus.CREATED)
    void algomaReception(@RequestParam("files") MultipartFile[] files, @RequestParam("username") String username, @RequestParam("password") String password) {
        LoginCredentials credentials = new LoginCredentials(username, password);
        browserAutomationService.createAlgomaReception(files, credentials);
    }

    @PostMapping("/shipment")
    @ResponseStatus(HttpStatus.CREATED)
    void shipment(
            @RequestParam("file") MultipartFile file,
            @RequestParam("timeIn") String timeIn,
            @RequestParam("driverName") String driverName,
            @RequestParam("license") String license,
            @RequestParam("truckingCompany") String truckingCompany,
            @RequestParam("gatepassNumber") String gatepassNumber,
            @RequestParam("dor") String dor,
            @RequestParam("username") String username,
            @RequestParam("passwordTC3") String passwordTC3,
            @RequestParam("passwordTM") String passwordTM
    ) {
        Gatepass gatepass = new Gatepass(file, timeIn, driverName, license.toUpperCase(Locale.ROOT), truckingCompany.toUpperCase(Locale.ROOT), gatepassNumber, dor.toUpperCase(Locale.ROOT));
        LoginCredentials tc3Credentials = new LoginCredentials(username + "@qsl.com", passwordTC3);
        LoginCredentials tmCredentials = new LoginCredentials(username, passwordTM);
        browserAutomationService.createShipment(gatepass, tc3Credentials, tmCredentials);
    }
}
