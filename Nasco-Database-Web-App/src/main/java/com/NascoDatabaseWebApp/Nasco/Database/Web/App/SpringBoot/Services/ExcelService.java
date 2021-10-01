package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.util.ExcelHelper;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class ExcelService {

    /*
    @Autowired
    RusalLineItemRepository repository;

    public ByteArrayInputStream load() {
        List<RusalLineItem> lineItems = repository.findAll();

        ByteArrayInputStream in = ExcelHelper.loadToExcel(lineItems);
        return in;
    }

    public ByteArrayInputStream loadByOrder(String order) {
        List<RusalLineItem> lineItems = repository.findByWorkOrder(order);

        ByteArrayInputStream in = ExcelHelper.loadToExcel(lineItems);
        return in;
    }

     */
}
