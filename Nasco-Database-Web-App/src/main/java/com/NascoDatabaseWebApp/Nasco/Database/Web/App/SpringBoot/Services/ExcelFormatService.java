package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.macros.AlgomaReport;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.macros.SsabReport;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


@Service
public class ExcelFormatService {

    public ByteArrayInputStream formatAlgomaReport(MultipartFile[] excelInventoryReport) {
        AlgomaReport algomaReport = new AlgomaReport();

        return algomaReport.createReport(excelInventoryReport);
    }

    public ByteArrayInputStream formatSsabReport(MultipartFile excelInventoryReport) {
        SsabReport ssabReport = new SsabReport();

        return ssabReport.createReport(new MultipartFile[] { excelInventoryReport });
    }

}
