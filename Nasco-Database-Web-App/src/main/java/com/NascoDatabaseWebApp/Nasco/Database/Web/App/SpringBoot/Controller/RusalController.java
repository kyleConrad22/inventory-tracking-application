package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Controller;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.RusalLineItemService;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/rusal")
public class RusalController {

    @Autowired
    private RusalLineItemService rusalLineItemService;

    @GetMapping
    Iterable<RusalLineItem> list() {
        return rusalLineItemService.findAll();
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
        return this.rusalLineItemService.save(
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
                .barge("N/A")
                .receptionDate("N/A")
                .checker("N/A")
                .mark("N/A")
                    .build());
    }

    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    void update(
        @RequestParam final String heatNum,
        @RequestParam final String workOrder,
        @RequestParam final String loadNum,
        @RequestParam final String loader,
        @RequestParam final String loadTime
    ) {
        rusalLineItemService.update(heatNum, workOrder, loadNum, loader, loadTime);
    }

    @GetMapping("/excel/download-all")
    ResponseEntity<Resource> downloadDatabaseCopy() {
        String fileName = "rusal-database-copy-" + LocalDate.now() + ".xlsx";
        InputStreamResource file = new InputStreamResource(rusalLineItemService.loadAll());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @GetMapping("excel/download-by-order-and-load")
    ResponseEntity<Resource> downloadByOrderAndLoad(@RequestParam final String workOrder, @RequestParam final String loadNum) {
        String fileName = "rusal-order-" + workOrder + "-load-" + loadNum + ".xlsx";
        InputStreamResource file = new InputStreamResource(rusalLineItemService.loadByOrderAndLoad(workOrder, loadNum));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @GetMapping("excel/download-by-barge")
    ResponseEntity<Resource> downloadByBarge(@RequestParam final String barge) {
        String fileName = "rusal-barge-" + barge + ".xlsx";
        InputStreamResource file = new InputStreamResource(rusalLineItemService.loadByBarge(barge));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

}
