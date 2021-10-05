package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.macros;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;

public class SsabReport extends CustomerReport {

    public SsabReport() {
        super(Collections.singletonList(AcceptedSheet.INVENTORY));
    }

    @Override
    public void parseFile(Workbook inWorkbook) {

    }
}
