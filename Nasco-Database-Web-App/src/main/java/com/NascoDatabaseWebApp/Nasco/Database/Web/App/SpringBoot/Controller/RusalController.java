package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Controller;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rusal")
public class RusalController {

    @Autowired
    private RusalLineItemRepository rusalLineItemRepository;

    @GetMapping
    Iterable<RusalLineItem> list() {
        return rusalLineItemRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RusalLineItem create(
        @RequestParam final String heatNum,
        @RequestParam final String packageNum,
        @RequestParam final String grossWeightKg,
        @RequestParam final String netWeightKg,
        @RequestParam final String quantity,
        @RequestParam final String dimension,
        @RequestParam final String grade,
        @RequestParam final String certificateNum,
        @RequestParam final String blNum,
        @RequestParam final String barcode
        ) {
        return this.rusalLineItemRepository.save(
            RusalLineItem.builder()
                .heatNum(heatNum)
                .packageNum(packageNum)
                .grossWeightKg(grossWeightKg)
                .netWeightKg(netWeightKg)
                .quantity(quantity)
                .dimension(dimension)
                .grade(grade)
                .certificateNum(certificateNum)
                .blNum(blNum)
                .barcode(barcode)
                .workOrder("N/A")
                .loadNum("N/A")
                .loader("N/A")
                .loadTime("N/A")
                .build());
    }
}
