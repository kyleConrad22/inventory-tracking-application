package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions.algoma;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AlgomaItem {
    private String address;
    private String receiver;
    private String poNumber;
    private String type;
    private String weight;
    private String thickness;
    private String diameter;
    private String heat;
    private String mark;
    private String itemNumber;
}
