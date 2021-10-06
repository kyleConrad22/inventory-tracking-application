package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.macros.AcceptedSheet;
import org.apache.poi.ss.usermodel.Sheet;

public class SheetContainer {

    public final AcceptedSheet sheetType;
    public final int sheetIndex;

    public SheetContainer(AcceptedSheet sheetType, int sheetIndex) {
        this.sheetType = sheetType;
        this.sheetIndex = sheetIndex;
    }
}
