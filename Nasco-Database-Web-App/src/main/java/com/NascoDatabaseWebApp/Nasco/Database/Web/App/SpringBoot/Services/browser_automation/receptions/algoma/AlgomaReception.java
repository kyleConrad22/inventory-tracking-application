package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions.algoma;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.PdfRelease;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions.Reception;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class AlgomaReception extends Reception implements PdfRelease {

    public void uploadReleases(MultipartFile[] files) {
        List<AlgomaRelease> orders = new ArrayList<>();
        for (MultipartFile file : files) {
            String convertedFile = convertToText(readFile(file));
            orders.add((AlgomaRelease) parseRelease(convertedFile));
        }
        startSession();
        loginTc3();
        for (AlgomaRelease order : orders) {
            createReception(order);
        }
        endSession();
    }

    public AlgomaRelease parseRelease(String convertedFile) {
        return null;
    }

    @Override
    protected void createReception(Release release) {

    }

    @Override
    protected XSSFWorkbook createImportManifest(Release release) {
        return null;
    }
}
