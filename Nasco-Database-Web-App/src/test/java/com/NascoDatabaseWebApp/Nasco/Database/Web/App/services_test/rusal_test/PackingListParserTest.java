package com.NascoDatabaseWebApp.Nasco.Database.Web.App.services_test.rusal_test;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.rusal.PackingListParser;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class PackingListParserTest {

    public static class GetNextLotAlphabeticIdentifierTest {

        @Test
        void givenEmptyList_Return_A() {
            assertEquals("A", PackingListParser.getNextLotAlphabeticIdentifier(Collections.emptyList()));
        }

        @Test
        void givenEntryWithoutHyphen_Return_A() {
            assertEquals("A", PackingListParser.getNextLotAlphabeticIdentifier(Collections.singletonList("A12")));
        }

        @Test
        void givenEntriesWithHyphen_With_IncorrectFirstStringFormatting_Return_A() {
            assertEquals("A", PackingListParser.getNextLotAlphabeticIdentifier(Collections.singletonList("A2-23")));
            assertEquals("A", PackingListParser.getNextLotAlphabeticIdentifier(Collections.singletonList("-")));
            assertEquals("A", PackingListParser.getNextLotAlphabeticIdentifier(Collections.singletonList("2-23")));
            assertEquals("A", PackingListParser.getNextLotAlphabeticIdentifier(Collections.singletonList("f-2")));
            assertEquals("A", PackingListParser.getNextLotAlphabeticIdentifier(Collections.singletonList("3A-2")));
            assertEquals("A", PackingListParser.getNextLotAlphabeticIdentifier(Collections.singletonList("-23")));
        }

        @Test
        void givenListWithSingleEntry() {
            assertEquals("B", PackingListParser.getNextLotAlphabeticIdentifier(Collections.singletonList("A-12")));
        }

        @Test
        void givenListWithMultipleEntries_Excluding_Z() {
            assertEquals("B", PackingListParser.getNextLotAlphabeticIdentifier(Arrays.asList("A-23", "A-2")));
            assertEquals("C", PackingListParser.getNextLotAlphabeticIdentifier(Arrays.asList("A-23", "B-1")));
            assertEquals("D", PackingListParser.getNextLotAlphabeticIdentifier(Arrays.asList("A-2", "B-5", "C-4")));
        }

        @Test
        void givenListWith_Z_AsEntry() {
            assertEquals("AA", PackingListParser.getNextLotAlphabeticIdentifier(Collections.singletonList("Z-01")));
        }

        /* TODO - ImplementDualAlphabeticIdentifierLogic
        @Test
        void givenListWithDualAlphabeticIdentifiers() {

            assertEquals("AB", PackingListParser.getNextLotAlphabeticIdentifier(Collections.singletonList("AA-23")));
            assertEquals("BA", PackingListParser.getNextLotAlphabeticIdentifier(Collections.singletonList("AZ-2")));
            assertEquals("BB", PackingListParser.getNextLotAlphabeticIdentifier(Collections.singletonList("BA-1")));
            assertEquals("BC", PackingListParser.getNextLotAlphabeticIdentifier(Arrays.asList("AC-34", "BB-12")));
        }
        */
    }
}
