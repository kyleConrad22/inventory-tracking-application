package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.receptions.algoma;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.browser_automation.util.Release;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AlgomaRelease extends Release {
    private String railcar;
    private String bolNumber;
    private String offloadDate;
    private List<AlgomaItem> items;

    public String toString() {
        return String.format(
                "Railcar: %s\n" +
                "BOL Number: %s\n" +
                "Offload Date: %s\n" +
                items.toString(),
            railcar, bolNumber, offloadDate
        );
    }
}
