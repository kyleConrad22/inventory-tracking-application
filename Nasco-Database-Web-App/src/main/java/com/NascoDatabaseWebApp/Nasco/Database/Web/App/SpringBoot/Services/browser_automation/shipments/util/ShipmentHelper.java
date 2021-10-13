package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.util;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.boscus.BoscusShipment;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.rusal.RusalShipment;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.Shipment;

public final class ShipmentHelper {
    public static Shipment getShipmentType(Gatepass gatepass) {

        String dor = gatepass.getDor();
        if (dor.charAt(0) == 'S') {
            return new BoscusShipment();
        } else if (dor.substring(0,3).equals("RAC")) {
            return new RusalShipment();
        } else {
            throw new RuntimeException("Unknown customer!");
        }
    }
}
