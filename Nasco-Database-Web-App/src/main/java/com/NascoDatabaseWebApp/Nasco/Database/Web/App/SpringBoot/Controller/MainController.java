package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Controller;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItemRepository;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.RusalLineItemService;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Controller
@RequestMapping(path = "/demo")
public class MainController {
    @Autowired
    private RusalLineItemRepository rusalLineItemRepository;

    @Autowired
    private RusalLineItemService rusalLineItemService;

    @RequestMapping (value = "/")
    public String index() {
        return "index";
    }

    @PostMapping(path = "/add")
    public @ResponseBody String addLineItem (
            @RequestParam String heatNum,
            @RequestParam String packageNum,
            @RequestParam String grossWeightKg,
            @RequestParam String netWeightKg,
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
        rusalLineItemService.addLineItem(
                heatNum, packageNum, grossWeightKg, netWeightKg, quantity, dimension, grade, certificateNum, blNum, barcode, workOrder, loadNum, loader, loadTime);
        return "Saved";
    }

    /*
    @GetMapping(path = "/update")
    public @ResponseBody String updateLineItem(
            @RequestParam String heatNum,
            @RequestParam String workOrder,
            @RequestParam String loadNum,
            @RequestParam String loader,
            @RequestParam String loadTime) {
        rusalLineItemService.updateRusalDatabase(heatNum, workOrder, loadNum, loader, loadTime);
        return "Updated";
    }
    */
}
