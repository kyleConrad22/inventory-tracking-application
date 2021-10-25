package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.LoginCredentials;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions.algoma.AlgomaReception;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BrowserAutomationService {
    public void createAlgomaReception(MultipartFile[] inFiles, LoginCredentials tc3Credentials) {
        AlgomaReception algomaReception = new AlgomaReception();
        algomaReception.uploadReleases(inFiles, tc3Credentials);
    }
}
