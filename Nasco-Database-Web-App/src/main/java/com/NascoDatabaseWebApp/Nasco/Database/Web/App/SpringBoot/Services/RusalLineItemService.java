package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItemRepository;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class RusalLineItemService implements RusalService {

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

    public List<RusalLineItem> findByBarge(String barge) {
        return repository.findByBarge(barge);
    }

    public void update(String heatNum, String workOrder, String loadNum, String loader, String loadTime) {
        repository.update(heatNum, workOrder, loadNum, loader, loadTime);
    }

    public ByteArrayInputStream loadAll() {
        return ExcelHelper.loadToExcel(findAll());
    }

    public ByteArrayInputStream loadByOrderAndLoad(String workOrder, String loadNum) {
        return ExcelHelper.loadToExcel(findByOrderAndLoad(workOrder, loadNum));
    }

    public ByteArrayInputStream loadByBarge(String barge) {
        return ExcelHelper.loadToExcel(findByBarge(barge));
    }

    public void addMark(String bl, String mark) {
        repository.addMark(bl, mark);
    }
    public void addBarge(String bl, String barge) {
        repository.addBarge(bl, barge);
    }
}
