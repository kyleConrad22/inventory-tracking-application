package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.rusal;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PackingListParser {

    private static final List<String> HEADERS = Arrays.asList(
            "Heat_number", "Package_No", "WEIGHT_GROSS", "WEIGHT", "DIMENSION", "Grade", "QUANTITY",
            "certificate_NO", "BL", "BARCODE"
    );

    public static List<RusalLineItem> parsePackingList(@NonNull MultipartFile file) {
        Workbook workbook = ExcelHelper.readExcelFile(file);

        Sheet sheet = workbook.getSheetAt(0);

        HashMap<String, Integer> headerColumnMap = getHeaderColumns(sheet, HEADERS);

        List<RusalLineItem> rusalLItems = getItemsFromWorkbook(sheet, headerColumnMap);

        return addLots(rusalLItems);
    }


    public static @NonNull List<RusalLineItem> getItemsFromWorkbook(@NonNull Sheet sheet, @NonNull HashMap<String, Integer> headerColumnMap) {
        return null;
    }

    /* Find column index which corresponds with each header in headers and returns a new HashMap with mappings*/
    public static @NonNull HashMap<String, Integer> getHeaderColumns(@NonNull Sheet sheet, @NonNull List<String> headers) {
        return null;
    }

    /* Add lot numbers for each Rusal item which has INGOT as its commodity typing returns list of Rusal items which have lot field set as non-empty*/
    private static @NonNull List<RusalLineItem> addLots(@NonNull List<RusalLineItem> rusalItems) {
        return null;
    }
}
