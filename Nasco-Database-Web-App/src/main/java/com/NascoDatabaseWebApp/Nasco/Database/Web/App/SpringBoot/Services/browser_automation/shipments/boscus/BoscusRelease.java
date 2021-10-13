package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.boscus;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.Release;
import lombok.*;

import java.util.List;

@Setter @Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class BoscusRelease extends Release {
    private String sku;
    private String po;
    private String receiver;
    private String receiverAddress;
    private List<BoscusItem> items;
}
