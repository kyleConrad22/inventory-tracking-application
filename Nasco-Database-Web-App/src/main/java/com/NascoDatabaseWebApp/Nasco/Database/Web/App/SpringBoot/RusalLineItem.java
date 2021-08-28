package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot;

import javax.persistence.*;

@Entity //Tells Hibernate to make a table out of this class
@Table(name = "current_inventory")
public class RusalLineItem {
    private String heatNum;

    private String packageNum;

    private String grossWeight;

    private String netWeight;

    private String quantity;

    private String dimension;

    private String grade;

    private String certificateNum;

    private String blNum;

    @Id
    private String barcode;

    public String getHeatNum(){
        return heatNum;
    }

    public String getPackageNum() {
        return packageNum;
    }

    public String getGrossWeight() {
        return grossWeight;
    }

    public String getNetWeight() {
        return netWeight;
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

    public void setGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }

    public void setPackageNum(String packageNum) {
        this.packageNum = packageNum;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
