package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.enums;

public enum Site {
    IROQUOIS_LANDING("Iroquois Landing");

    private final String string;

    Site(String name) {
        string = name;
    }

    @Override
    public String toString() {
        return string;
    }
}
