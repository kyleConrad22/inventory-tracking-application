package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.AutomatedSession;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.Release;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class Reception extends AutomatedSession {
    protected abstract void createReception(Release release);
    protected abstract XSSFWorkbook createImportManifest(Release release);

}
