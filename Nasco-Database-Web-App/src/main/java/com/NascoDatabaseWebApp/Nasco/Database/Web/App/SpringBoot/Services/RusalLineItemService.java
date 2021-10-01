package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItemRepository;
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
        repository.save(new RusalLineItem(heatNum, packageNum, grossWeightKg, netWeightKg, quantity, dimension, grade, certificateNum, blNum, barcode, workOrder, loadNum, loader, loadTime));
    }

    /*
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
    */
}
