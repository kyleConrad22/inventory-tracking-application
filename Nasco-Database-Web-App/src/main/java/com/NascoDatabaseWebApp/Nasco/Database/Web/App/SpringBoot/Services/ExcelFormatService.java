package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class ExcelFormatService {

    public ByteArrayInputStream formatAlgomaReport(MultipartFile excelInventoryReport) {
        List<String> headers = Arrays.asList("Client Inventory", "Order", "Receiver", "Heat Number", "Mark", "Scope", "Other", "Weight per Unit", "Dimensions");
        HashMap<String, String> replace = new HashMap<>();
        replace.put("Other", "Date Received");

        return ExcelHelper.formatAlgomaReport(excelInventoryReport, 2, headers, replace);
    }

    public ByteArrayInputStream formatSsabReport(MultipartFile excelInventoryReport) {
        List<String> headers = Arrays.asList("Client Name", "Order", "Receiver", "Product Type", "Quantity", "PO Number", "Lot Number", "Mark", "Other", "Weight per Unit", "Total Weight", "Dimensions", "Quantity per Package");

        return ExcelHelper.formatSsabReport(excelInventoryReport, 0, headers);
    }

}
