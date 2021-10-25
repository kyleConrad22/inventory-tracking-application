package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor
public class RusalShipmentUpdateParams {
    private String heatNum;
    private String workOrder;
    private String loadNum;
    private String loader;
    private String loadTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RusalShipmentUpdateParams that = (RusalShipmentUpdateParams) o;
        return Objects.equals(heatNum, that.heatNum) && Objects.equals(workOrder, that.workOrder) && Objects.equals(loadNum, that.loadNum) && Objects.equals(loader, that.loader) && Objects.equals(loadTime, that.loadTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(heatNum, workOrder, loadNum, loader, loadTime);
    }
}
