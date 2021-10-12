package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions.algoma;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AlgomaItem {
    private String receiver;
    private String order;
    private String poNumber;
    private String type;
    private String weight;
    private String thickness;
    private String width;
    private String heat;
    private String mark;
    private String itemNumber;

    public String toString() {
        return String.format(
                "Receiver: %s\n" +
                "Order: %s\n" +
                "PO Number: %s\n" +
                "Subtype: %s\n" +
                "Weight: %s\n" +
                "Thickness: %s\n" +
                "Diameter: %s\n" +
                "Heat: %s\n" +
                "Mark: %s\n" +
                "Item Number: %s",
            receiver, order, poNumber, type, weight, thickness, width, heat, mark, itemNumber);
    }
}
