package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Controller;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.BrowserAutomationService;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.LoginCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/browser")
public class BrowserAutomationController {

    @Autowired
    private BrowserAutomationService browserAutomationService;

    @PostMapping("/reception/algoma")
    @ResponseStatus(HttpStatus.CREATED)
    void algomaReception(@RequestParam("files") MultipartFile[] files, @RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("offloadDate") String offloadDate) {
        LoginCredentials credentials = new LoginCredentials(username, password);
        browserAutomationService.createAlgomaReception(files, credentials, offloadDate);
    }
}
