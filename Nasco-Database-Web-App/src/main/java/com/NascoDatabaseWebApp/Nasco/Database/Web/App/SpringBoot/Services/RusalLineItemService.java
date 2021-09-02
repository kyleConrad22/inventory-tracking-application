package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.RusalLineItem;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.RusalLineItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RusalLineItemService {
    @Autowired
    RusalLineItemRepository repository;

    public void addLineItem(
            String heatNum,
            String packageNum,
            String netWeightKg,
            String grossWeightKg,
            String quantity,
            String dimension,
            String grade,
            String certificateNum,
            String blNum,
            String barcode,
            String workOrder,
            String loadNum,
            String loader,
            String loadTime
    ) {
        RusalLineItem lineItem = new RusalLineItem();
        lineItem.setHeatNum(heatNum);
        lineItem.setPackageNum(packageNum);
        lineItem.setNetWeightKg(netWeightKg);
        lineItem.setGrossWeightKg(grossWeightKg);
        lineItem.setQuantity(quantity);
        lineItem.setDimension(dimension);
        lineItem.setGrade(grade);
        lineItem.setCertificateNum(certificateNum);
        lineItem.setBlNum(blNum);
        lineItem.setBarcode(barcode);
        lineItem.setWorkOrder(workOrder);
        lineItem.setLoadNum(loadNum);
        lineItem.setLoader(loader);
        lineItem.setLoadTime(loadTime);
        repository.save(lineItem);
    }

    public void updateRusalDatabase(
            String heatNum,
            String workOrder,
            String loadNum,
            String loader,
            String loadTime
    ) {
        RusalLineItem lineItem = repository.findByHeatNum(heatNum);
        lineItem.setWorkOrder(workOrder);
        lineItem.setLoadNum(loadNum);
        lineItem.setLoader(loader);
        lineItem.setLoadTime(loadTime);
        repository.save(lineItem);
    }
}
