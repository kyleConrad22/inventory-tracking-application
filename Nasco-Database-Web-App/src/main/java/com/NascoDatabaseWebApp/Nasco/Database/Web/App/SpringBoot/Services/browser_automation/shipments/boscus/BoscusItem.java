package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.boscus;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class BoscusItem {
    private String pieceCount;
    private String quantity;
    private String size;

    public boolean isDuplicate(BoscusItem item) {
        return pieceCount.equals(item.getPieceCount()) && size.equals(item.getSize());
    }
}
