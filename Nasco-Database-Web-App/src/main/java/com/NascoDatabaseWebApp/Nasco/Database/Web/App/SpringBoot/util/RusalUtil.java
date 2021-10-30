package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class RusalUtil {

    public static List<RusalLineItem> sortDescendingDateTime(List<RusalLineItem> items) {
        ArrayList<RusalLineItem> arrayList = new ArrayList<>(items);

        if (items.size() < 2) return items;

        for (int i = 2; i < items.size(); i++) {

            RusalLineItem keyItem = arrayList.get(i);
            LocalDateTime key = getLatestUpdateTime(keyItem);

            int j = i - 1;
            while (j > -1 && key.compareTo(getLatestUpdateTime(arrayList.get(j))) > 0) {
                arrayList.set(j + 1, arrayList.get(j--));
            }
            arrayList.set(j + 1, keyItem);
        }
        return arrayList;
    }

    // Returns either the reception time or load time of a rusal object, whichever is the most recently updated time
    private static LocalDateTime getLatestUpdateTime(RusalLineItem item) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

        try {
            if (!item.getLoadTime().equals("")) {
                return LocalDateTime.parse(item.getLoadTime(), formatter);
            } else {
                return LocalDateTime.parse(item.getReceptionDate(), formatter);
            }
        } catch (DateTimeParseException e) {
            return LocalDateTime.MIN;
        }
    }
}
