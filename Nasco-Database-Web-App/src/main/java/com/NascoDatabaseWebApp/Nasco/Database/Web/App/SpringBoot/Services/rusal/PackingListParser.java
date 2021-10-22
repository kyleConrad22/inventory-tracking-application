package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.rusal;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.RusalField;
import org.apache.poi.ss.usermodel.*;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper.getCellValueAsString;

public final class PackingListParser {

    private static final List<String> HEADERS = Arrays.asList(
            "Heat_number", "Package_No", "WEIGHT_GROSS", "WEIGHT", "DIMENSION", "Grade", "QUANTITY",
            "certificate_NO", "BL", "BARCODE"
    );

    public static List<RusalLineItem> parsePackingList(@NonNull MultipartFile file, List<String> lotIdentifiers) {
        Workbook workbook = ExcelHelper.readExcelFile(file);

        Sheet sheet = workbook.getSheetAt(0);

        HashMap<Integer, String> columnHeaderMap = getHeaderColumns(sheet, HEADERS);

        List<RusalLineItem> rusalLItems = getItemsFromWorkbook(sheet, columnHeaderMap);

        return addLots(rusalLItems, lotIdentifiers);
    }


    public static @NonNull List<RusalLineItem> getItemsFromWorkbook(@NonNull Sheet sheet, @NonNull HashMap<Integer, String> headerColumnMap) {
        List<RusalLineItem> items = new ArrayList<>();

        IntStream.range(1, sheet.getLastRowNum() + 1).forEach(rowInd -> {
            Row row = sheet.getRow(rowInd);

            HashMap<RusalField, String> fieldValueMap = new HashMap<>();

            IntStream.range(0, row.getLastCellNum() + 1).forEach(colInd -> {
                if (headerColumnMap.containsKey(colInd)) {
                    String cellValue = getCellValueAsString(row.getCell(rowInd));

                    switch (headerColumnMap.get(colInd)) {
                        case "Heat_number":
                            fieldValueMap.put(RusalField.HEAT, cellValue);
                            break;

                        case "Package_No":
                            fieldValueMap.put(RusalField.PACKAGE, cellValue);
                            break;

                        case "WEIGHT_GROSS":
                            fieldValueMap.put(RusalField.GROSS_WEIGHT, cellValue);
                            break;

                        case "WEIGHT":
                            fieldValueMap.put(RusalField.NET_WEIGHT, cellValue);
                            break;

                        case "Dimension":
                            fieldValueMap.put(RusalField.DIMENSION, cellValue);
                            break;

                        case "Grade":
                            fieldValueMap.put(RusalField.GRADE, cellValue);
                            break;

                        case "QUANTITY":
                            fieldValueMap.put(RusalField.QUANTITY, cellValue);
                            break;

                        case "certificate_NO":
                            fieldValueMap.put(RusalField.CERTIFICATE, cellValue);
                            break;

                        case "BL":
                            fieldValueMap.put(RusalField.BL, cellValue);
                            break;

                        case "BARCODE":
                            fieldValueMap.put(RusalField.BARCODE, cellValue);
                            break;
                    }
                }
            });

            items.add(RusalLineItem.builder()
                    .heatNum(fieldValueMap.get(RusalField.HEAT))
                    .blNum(fieldValueMap.get(RusalField.BL))
                    .barcode(fieldValueMap.get(RusalField.BARCODE))
                    .grade(fieldValueMap.get(RusalField.GRADE))
                    .dimension(fieldValueMap.get(RusalField.DIMENSION))
                    .grossWeightKg(fieldValueMap.get(RusalField.GROSS_WEIGHT))
                    .netWeightKg(fieldValueMap.get(RusalField.NET_WEIGHT))
                    .certificateNum(fieldValueMap.get(RusalField.CERTIFICATE))
                    .packageNum(fieldValueMap.get(RusalField.PACKAGE))
                    .quantity(fieldValueMap.get(RusalField.QUANTITY))
                    .build());
        });

        return items;
    }

    /* Find column index which corresponds with each header in headers and returns a new HashMap with mappings*/
    public static @NonNull HashMap<Integer, String> getHeaderColumns(@NonNull Sheet sheet, @NonNull List<String> headers) {
        HashMap<Integer, String> hm = new HashMap<>();
        Row firstRow = sheet.getRow(0);

        for (int i = 0; i < firstRow.getLastCellNum() + 1; i++) {
            Cell cell = firstRow.getCell(i);

            if (cell.getCellType() != CellType.STRING) {
                String cellValue = cell.getStringCellValue();

                if (headers.contains(cellValue) && !hm.containsValue(cellValue)) {
                    hm.put(i, cellValue);
                }

            } else {
                throw new RuntimeException("Sheet not of correct format, header row of sheet should contain string values!");
            }
        }

        return hm;
    }

    /* Add lot numbers for each Rusal item which has INGOT as its commodity typing returns list of Rusal items which have lot field set as non-empty*/
    private static @NonNull List<RusalLineItem> addLots(@NonNull List<RusalLineItem> rusalItems, List<String> lotIdentifiers) {

        String lotIdentifier = getNextLotAlphabeticIdentifier(lotIdentifiers);

        AtomicInteger i = new AtomicInteger(1);
        HashMap<String, String> heatLotMap = new HashMap<>();

        rusalItems.forEach(item -> {
            if (item.getGrade().contains("INGOTS")) {
                String baseHeat = getBaseHeat(item.getHeatNum());

                if (!heatLotMap.containsKey(baseHeat)) {
                    heatLotMap.put(baseHeat, lotIdentifier + "-" + i.getAndIncrement());
                }

                item.setLot(heatLotMap.get(baseHeat));

            }
        });

        return rusalItems;
    }

    public static String getNextLotAlphabeticIdentifier(List<String> lotIdentifiers) {
        AtomicInteger largestLastCharAscii = new AtomicInteger(0);
        AtomicInteger firstCharAscii = new AtomicInteger(0);

        lotIdentifiers.forEach(identifier -> {
            if (!identifier.isEmpty()) {
                if (identifier.contains("-") && identifier.split("-").length > 0) {
                    String alphabeticIdentifier = identifier.split("-")[0];

                    if (alphabeticIdentifier.length() > 0) {

                        boolean isValidFormat = true;
                        for (char c : alphabeticIdentifier.toCharArray()) {
                            if (((int) c) > 90 || ((int) c) < 65 ) {
                                isValidFormat = false;
                                break;
                            }
                        }

                        if (isValidFormat) {
                            int ascii = alphabeticIdentifier.charAt(alphabeticIdentifier.length() - 1);
                            if (largestLastCharAscii.get() < ascii) {
                                largestLastCharAscii.set(ascii);
                            }
                        }
                    }
                }
            }
        });

        if (largestLastCharAscii.get() == 0) {
            return "A";
        }
        if (firstCharAscii.get() == 0) {
            if (largestLastCharAscii.get() == 90) {
                return "AA";
            }
            return (char)largestLastCharAscii.addAndGet(1) + "";
        }

        /* TODO - Add logic to set lot identifiers when there are more than 1 letters in identifier i.e. AA, AB, ect. */
        return "AA";

    }

    private static String getBaseHeat(String heat) {
        return heat.substring(0, 6);
    }
}
