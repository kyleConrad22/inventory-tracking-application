package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;

import java.util.List;

public interface RusalService {

    RusalLineItem save(RusalLineItem rusalLineItem);
    List<RusalLineItem> findAll();
    List<RusalLineItem> findByOrderAndLoad(String workOrder, String loadNum);
    void update(String heatNum, String workOrder, String loadNum, String loader, String loadTime);
}
