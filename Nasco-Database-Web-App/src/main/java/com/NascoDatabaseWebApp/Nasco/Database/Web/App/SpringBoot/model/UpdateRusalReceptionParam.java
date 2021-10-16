package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model;

import java.util.Objects;

public class UpdateRusalReceptionParam {
    private String heatNum;
    private String checker;
    private String receptionDate;

    public String getChecker() {
        return checker;
    }

    public String getHeatNum() {
        return heatNum;
    }

    public String getReceptionDate() {
        return receptionDate;
    }

    public void setChecker(String checker) {
        this.checker = checker;
    }

    public void setHeatNum(String heatNum) {
        this.heatNum = heatNum;
    }

    public void setReceptionDate(String receptionDate) {
        this.receptionDate = receptionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateRusalReceptionParam that = (UpdateRusalReceptionParam) o;
        return heatNum.equals(that.heatNum) && checker.equals(that.checker) && receptionDate.equals(that.receptionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(heatNum, checker, receptionDate);
    }

}
