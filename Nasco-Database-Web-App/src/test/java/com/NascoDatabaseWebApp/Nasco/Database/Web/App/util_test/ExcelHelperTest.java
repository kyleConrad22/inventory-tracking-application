package com.NascoDatabaseWebApp.Nasco.Database.Web.App.util_test;



import static org.junit.jupiter.api.Assertions.assertEquals;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

public class ExcelHelperTest {

    public static class RemoveFirstRowsTests {
        private Sheet sheet;

        @BeforeEach
        void init() {
            Workbook workbook = new XSSFWorkbook();
            sheet = workbook.createSheet();
            IntStream.range(0,0);
        }

        @Test
        void removeFirstRowsLessThanOneTest() {
            int expected = ExcelHelper.getFirstNullRow(sheet);
            ExcelHelper.removeFirstRows(sheet,-1);

            assertEquals(expected, ExcelHelper.getFirstNullRow(sheet));
        }

        @Test
        void removeFirstRowsSingleTest() {

        }

        @Test
        void removeFirstRowsLargeMultipleTest() {

        }
    }

}
