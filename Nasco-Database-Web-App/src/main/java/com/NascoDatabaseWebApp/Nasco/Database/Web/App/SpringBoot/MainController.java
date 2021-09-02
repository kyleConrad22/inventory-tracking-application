package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot;

import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Controller
@RequestMapping(path = "/demo")
public class MainController {
    @Autowired
    private RusalLineItemRepository rusalLineItemRepository;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public @ResponseBody String addLineItem (
            @RequestParam String heatNum,
            @RequestParam String packageNum,
            @RequestParam String grossWeight,
            @RequestParam String netWeight,
            @RequestParam String quantity,
            @RequestParam String dimension,
            @RequestParam String grade,
            @RequestParam String certificateNum,
            @RequestParam String blNum,
            @RequestParam String barcode,
            @RequestParam String workOrder,
            @RequestParam String loadNum,
            @RequestParam String loader,
            @RequestParam String loadTime
            ) {

        RusalLineItem lineItem = new RusalLineItem();
        lineItem.setHeatNum(heatNum);
        lineItem.setPackageNum(packageNum);
        lineItem.setGrossWeight(grossWeight);
        lineItem.setNetWeight(netWeight);
        lineItem.setQuantity(quantity);
        lineItem.setDimension(dimension);
        lineItem.setGrade(grade);
        lineItem.setCertificateNum(certificateNum);
        lineItem.setBlNum(blNum);
        lineItem.setBarcode(barcode);

        rusalLineItemRepository.save(lineItem);
        return "Saved";
    }

    @GetMapping(path = "/all")
    public @ResponseBody List<RusalLineItem> getAllLineItems() {
        return rusalLineItemRepository.findAll();
    }

}
