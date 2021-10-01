package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.RusalLineItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// To be used for testing React UI when not connected to main database
@Component
public class DatabaseLoader implements CommandLineRunner {

    private final RusalLineItemService service;

    @Autowired
    public DatabaseLoader(RusalLineItemService service) {
        this.service = service;
    }

    @Override
    public void run(String... strings) throws Exception {
        List<String> heatNums = Arrays.asList("175553-23", "143553-55", "553332-43");
        List<String> packageNums = Arrays.asList("05", "15", "22");
        List<String> grossWeightKgs = Arrays.asList("655", "656", "1023");
        List<String> netWeightKgs = Arrays.asList("654", "655", "1022");
        List<String> quantities = Arrays.asList("44","44","5");
        List<String> dimensions = Arrays.asList("840X720X720", "840X720X720", "228X7000");
        List<String> grades = Arrays.asList("356.2 (SR) INGOTS", "356.2 (SR) INGOTS", "6063 LFE BILLETS");
        List<String> certificateNums = Arrays.asList("304939194","304939194", "304925953");
        List<String> blNums = Arrays.asList("SPNL06A21009", "SPNL06A21018", "THZS21025STPNWO21");
        List<String> barcodes = Arrays.asList("0120752099220", "0120752099232", "0120752093520");
        List<String> workOrders = Arrays.asList("RAC-015222", "RAC-015222", "N/A");
        List<String> loadNums = Arrays.asList("2", "3", "N/A");
        List<String> loaders = Arrays.asList("Frodo Baggins", "Bilbo Baggins", "N/A");
        List<String> loadTimes = Arrays.asList("09/29/2021 03:21 PM", "09/30/2021 09:24 AM", "N/A");

        for (int i = 0; i < heatNums.size(); i++) {
            this.service.addLineItem(
                    heatNums.get(i),
                    packageNums.get(i),
                    grossWeightKgs.get(i),
                    netWeightKgs.get(i),
                    quantities.get(i),
                    dimensions.get(i),
                    grades.get(i),
                    certificateNums.get(i),
                    blNums.get(i),
                    barcodes.get(i),
                    workOrders.get(i),
                    loadNums.get(i),
                    loaders.get(i),
                    loadTimes.get(i)
            );
        }
    }
}
