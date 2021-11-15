package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Controller;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.macros.ExcelFormatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Controller
@RequestMapping("/api/excel")
public class ExcelController {

    @Autowired
    private ExcelFormatService excelFormatService;

    @PostMapping("/algoma")
    ResponseEntity<Resource> downloadAlgomaReport(@RequestParam("file") MultipartFile[] excelDataFile) {
        String fileName = "algoma-inventory-report-" + LocalDate.now() + ".xlsx";
        InputStreamResource file = new InputStreamResource(excelFormatService.formatAlgomaReport(excelDataFile));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @PostMapping("/ssab")
    ResponseEntity<Resource> downloadSsabReport(@RequestParam("file") MultipartFile excelDataFile) {
        String fileName = "ssab-inventory-report-" + LocalDate.now() + ".xlsx";
        InputStreamResource file = new InputStreamResource(excelFormatService.formatSsabReport(excelDataFile));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);

    }

}


