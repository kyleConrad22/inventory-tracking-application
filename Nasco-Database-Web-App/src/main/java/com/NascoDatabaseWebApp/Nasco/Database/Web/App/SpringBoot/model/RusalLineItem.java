package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Builder
@Data
@Entity //Tells Hibernate to make a table out of this class
@Table(name="current_inventory")
@NoArgsConstructor
@AllArgsConstructor
public class RusalLineItem {
    private String heatNum;
    private String packageNum;
    private String grossWeightKg;
    private String netWeightKg;
    private String quantity;
    private String dimension;
    private String grade;
    private String certificateNum;
    private String blNum;
    private @Id String barcode;
    private String workOrder;
    private String loadNum;
    private String loader;
    private String loadTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null && getClass() != o.getClass()) return false;
        RusalLineItem rusalLineItem = (RusalLineItem) o;
        return Objects.equals(heatNum, rusalLineItem.heatNum) &&
                Objects.equals(grossWeightKg, rusalLineItem.grossWeightKg) &&
                Objects.equals(netWeightKg, rusalLineItem.netWeightKg) &&
                Objects.equals(quantity, rusalLineItem.quantity) &&
                Objects.equals(dimension, rusalLineItem.dimension) &&
                Objects.equals(grade, rusalLineItem.grade) &&
                Objects.equals(certificateNum, rusalLineItem.certificateNum) &&
                Objects.equals(blNum, rusalLineItem.certificateNum) &&
                Objects.equals(barcode, rusalLineItem.barcode) &&
                Objects.equals(workOrder, rusalLineItem.workOrder) &&
                Objects.equals(loadNum, rusalLineItem.loadNum) &&
                Objects.equals(loader, rusalLineItem.loader) &&
                Objects.equals(loadTime, rusalLineItem.loadTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(heatNum, grossWeightKg, netWeightKg, quantity, dimension, grade, certificateNum, blNum, barcode, workOrder, loadNum, loader, loadTime);
    }

    @Override
    public String toString() {
        return "RusalLineItem{" +
                ", heatNum='" + heatNum + '\'' +
                ", grossWeightKg='" + grossWeightKg + '\'' +
                ", netWeightKg='" + netWeightKg + '\'' +
                ", quantity='" + quantity + '\'' +
                ", dimension='" + dimension + '\'' +
                ", grade='" + grade + '\'' +
                ", certificateNum='" + certificateNum + '\'' +
                ", blNum='" + blNum + '\'' +
                ", barcode='" + barcode + '\'' +
                ", workOrder='" + workOrder + '\'' +
                ", loadNum='" + loadNum + '\'' +
                ", loader='" + loader + '\'' +
                ", loadTime='" + loadTime + '\'' +
                '}';
    }

    public String getHeatNum(){
        return heatNum;
    }

    public String getPackageNum() {
        return packageNum;
    }

    public String getGrossWeightKg() {
        return grossWeightKg;
    }

    public String getNetWeightKg () {
        return netWeightKg;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDimension() {
        return dimension;
    }

    public String getGrade() {
        return grade;
    }

    public String getCertificateNum() {
        return certificateNum;
    }

    public String getBlNum() {
        return blNum;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getWorkOrder() {
        if (workOrder != null) {
            return workOrder;
        } else {
            return "null";
        }
    }

    public String getLoadNum() {
        if (loadNum != null) {
            return loadNum;
        } else {
            return "null";
        }
    }

    public String getLoader() {
        if (loader != null) {
            return loader;
        } else {
            return "null";
        }
    }

    public String getLoadTime() {
        if (loadTime != null) {
            return loadTime;
        } else {
            return "null";
        }
    }

    public void setHeatNum(String heatNum) {
        this.heatNum = heatNum;
    }

    public void setBlNum(String blNum) {
        this.blNum = blNum;
    }

    public void setCertificateNum(String certificateNum) {
        this.certificateNum = certificateNum;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setGrossWeightKg(String grossWeightKg) {
        this.grossWeightKg = grossWeightKg;
    }

    public void setNetWeightKg(String netWeightKg) {
        this.netWeightKg = netWeightKg;
    }

    public void setPackageNum(String packageNum) {
        this.packageNum = packageNum;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setWorkOrder(String workOrder) {
        this.workOrder = workOrder;
    }

    public void setLoadNum(String loadNum) {
        this.loadNum = loadNum;
    }

    public void setLoader(String loader) {
        this.loader = loader;
    }

    public void setLoadTime(String loadTime) {
        this.loadTime = loadTime;
    }
}
