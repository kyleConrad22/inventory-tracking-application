package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RusalLineItemService implements RusalService{

    @Autowired
    RusalLineItemRepository repository;

    public RusalLineItem save(RusalLineItem rusalLineItem) {
        return repository.save(rusalLineItem);
    }

    public List<RusalLineItem> findAll() {
        return repository.findAll();
    }

    public List<RusalLineItem> findByOrderAndLoad(String workOrder, String loadNum) {
        return repository.findByOrderAndLoad(workOrder, loadNum);
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
