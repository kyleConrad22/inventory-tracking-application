package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.rusal;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.*;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

import static com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.rusal.PackingListParser.parsePackingList;
import static com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.RusalUtil.sortDescendingDateTime;

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

    public void update(String heatNum, String workOrder, String loadNum, String loader, String loadTime) {
        repository.updateShipment(heatNum, workOrder, loadNum, loader, loadTime);
    }

    public ByteArrayInputStream loadAll() {
        return ExcelHelper.loadToExcel(findAll());
    }

    public ByteArrayInputStream loadByOrderAndLoad(String workOrder, String loadNum) {
        return ExcelHelper.loadToExcel(repository.findByOrderAndLoad(workOrder, loadNum));
    }

    public ByteArrayInputStream loadByBarge(String barge) {
        return ExcelHelper.loadToExcel(repository.findByBarge(barge));
    }

    public void addMark(String bl, String mark) {
        repository.addMark(bl, mark);
    }

    public void addBarge(String bl, String barge) {
        repository.addBarge(bl, barge);
    }

    @Override
    public void updateReception(List<RusalReceptionUpdateParams> updateParams) {
        updateParams.forEach(it ->
            repository.updateReception(it.getHeatNum(), it.getReceptionDate(), it.getChecker())
        );
    }

    public void addLot(LotUpdateParams updateParams) {
        repository.addLot(updateParams.getLot(), updateParams.getBl(), updateParams.getHeat());
    }

    public void addLotSite(String lot, String bl, String heat) {
        repository.addLot(lot, bl, heat);
    }

    public void importPackingList(MultipartFile file, String barge) {
        List<RusalLineItem> rusalItems = parsePackingList(file, getUniqueLots());
        rusalItems.forEach(item -> {
            item.setBarge(barge);
            repository.save(item);
        });
    }

    public List<String> getUniqueLots() {
        return repository.getUniqueLots();
    }

    public void updateShipment(List<RusalShipmentUpdateParams> updateParams) {
        updateParams.forEach(it ->
            repository.updateShipment(it.getHeatNum(), it.getWorkOrder(), it.getLoadNum(), it.getLoader(), it.getLoadTime())
        );
    }

    public void insertItems(List<RusalLineItem> items) {
        items.forEach(item ->
            repository.save(item)
        );
    }

    public List<RusalLineItem> findRecent() {
        List<RusalLineItem> items = sortDescendingDateTime(repository.findReceivedItems());

        if (items.size() < 100) return items;
        return items.subList(0, 100);
    }
}
