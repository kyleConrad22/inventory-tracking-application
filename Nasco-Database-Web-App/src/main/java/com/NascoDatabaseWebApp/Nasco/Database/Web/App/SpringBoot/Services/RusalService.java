package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public interface RusalService {

    RusalLineItem save(RusalLineItem rusalLineItem);
    List<RusalLineItem> findAll();
    List<RusalLineItem> findByOrderAndLoad(String workOrder, String loadNum);
    void update(String heatNum, String workOrder, String loadNum, String loader, String loadTime);
    ByteArrayInputStream loadAll();
    ByteArrayInputStream loadByOrderAndLoad(String workOrder, String loadNum);
}
