package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.rusal;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.LotUpdateParams;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalReceptionUpdateParams;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalShipmentUpdateParams;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public interface RusalService {

    RusalLineItem save(RusalLineItem rusalLineItem);
    List<RusalLineItem> findAll();
    void update(String heatNum, String workOrder, String loadNum, String loader, String loadTime);
    ByteArrayInputStream loadAll();
    ByteArrayInputStream loadByOrderAndLoad(String workOrder, String loadNum);
    ByteArrayInputStream loadByBarge(String barge);
    void addMark(String bl, String mark);
    void addBarge(String bl, String mark);
    void updateReception(List<RusalReceptionUpdateParams> updateParams);
    void addLot(LotUpdateParams updateParams);
    void addLotSite(String lot, String bl, String heat);
    void importPackingList(MultipartFile file, String barge);
    List<String> getUniqueLots();
    void updateShipment(List<RusalShipmentUpdateParams> updateParams);
    void insertItems(List<RusalLineItem> items);
    List<RusalLineItem> findRecent();
    String getReceptionProgress(String barge);
}
