package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public final class StringHelper {
    public static void printOutput(String str) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            writer.write(str);

        } catch (IOException e) {
            throw new RuntimeException("An exception occurred while attempting to write to file: " + e.getMessage());
        }
    }

}
