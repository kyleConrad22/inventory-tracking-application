package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Controller;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.ExcelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/excel")
public class ExcelController {
    @Autowired
    ExcelService fileService;
/*
    @GetMapping("/download")
    public ResponseEntity<Resource> getFile(){
        String fileName = "RusalDatabase.xlsx";
        InputStreamResource file = new InputStreamResource(fileService.load());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @GetMapping("/download-by-order")
    public ResponseEntity<Resource> getFileByOrder(@RequestParam String order) throws JsonMappingException, JsonProcessingException {
        String fileName = "Order_" + order + ".xlsx";
        InputStreamResource file = new InputStreamResource(fileService.loadByOrder(order));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

 */
}