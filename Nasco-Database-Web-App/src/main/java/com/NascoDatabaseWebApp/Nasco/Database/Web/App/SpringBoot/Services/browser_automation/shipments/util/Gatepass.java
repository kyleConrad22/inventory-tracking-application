package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.util;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Gatepass {
    private MultipartFile file;
    private String inTime;
    private String driverName;
    private String license;
    private String truckingCompany;
    private String gatepassNumber;
    private String dor;

    public String toString() {
        boolean containsFile = file != null;
        return String.format(
                "Contains file: %s\nIn Time: %s\nDriver Name: %s\nLicense Number: %s\nTrucking Company: %s\nGatepass Number: %s\nDor: %s\n",
                containsFile, inTime, driverName, license, truckingCompany, gatepassNumber, dor
        );
    }
}
