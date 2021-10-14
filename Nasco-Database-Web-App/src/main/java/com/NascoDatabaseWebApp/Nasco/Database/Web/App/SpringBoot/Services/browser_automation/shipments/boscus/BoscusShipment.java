package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.boscus;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.FileFormatException;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.Release;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.shipments.ShipmentWithRelease;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.RegexHelper.collectMatches;

public class BoscusShipment extends ShipmentWithRelease {

    @Override
    protected String getRemarks(Release release, String clerkInitials) {
        return String.format(
                "%s\nPO#: %s\nSKU#: %s\nWeights are estimates only and as such are subject to review and revision as necessary.\n%s",
                release.getOrder(), ((BoscusRelease) release).getPo(), ((BoscusRelease) release).getSku(), clerkInitials
                );
    }

    /* TODO */
    @Override
    protected void navigateToLoadingRequestOrShippedItems() {

    }

    @Override
    public Release parseRelease(String release) {
        return new BoscusRelease(release);
    }

    /* TODO */
    @Override
    protected void addItemsToShipment(Release release) {

    }

}
