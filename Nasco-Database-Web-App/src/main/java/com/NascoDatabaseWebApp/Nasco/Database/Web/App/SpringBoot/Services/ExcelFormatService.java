package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.macros.AlgomaReport;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.macros.SsabReport;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;

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
