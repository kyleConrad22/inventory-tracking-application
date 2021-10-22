package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Controller;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.Services.rusal.RusalLineItemService;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.LotUpdateParams;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalReceptionUpdateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rusal")
public class RusalController {

    @Autowired
    private RusalLineItemService rusalLineItemService;

    @GetMapping
    @ResponseBody
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
        @RequestParam final String barcode,
        @RequestParam final String workOrder,
        @RequestParam final String loadNum,
        @RequestParam final String loader,
        @RequestParam final String loadTime,
        @RequestParam final String barge,
        @RequestParam final String mark,
        @RequestParam final String receptionDate,
        @RequestParam final String checker
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
                .workOrder(workOrder)
                .loadNum(loadNum)
                .loader(loader)
                .loadTime(loadTime)
                .barge(barge)
                .receptionDate(receptionDate)
                .checker(checker)
                .mark(mark)
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

    @GetMapping("/excel/download-by-barge")
    ResponseEntity<Resource> downloadByBarge(@RequestParam final String barge) {
        String fileName = "rusal-barge-" + barge + ".xlsx";
        InputStreamResource file = new InputStreamResource(rusalLineItemService.loadByBarge(barge));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @PostMapping("/import/packing-list")
    @ResponseStatus(HttpStatus.CREATED)
    void importPackingList(@RequestParam final MultipartFile file, @RequestParam final String barge) {
        rusalLineItemService.importPackingList(file, barge);
    }


    @PostMapping("/update/mark")
    @ResponseStatus(HttpStatus.OK)
    void addMark(@RequestParam final String bl, @RequestParam final String mark) {
        rusalLineItemService.addMark(bl, mark);
    }

    @PostMapping("/update/lot")
    @ResponseStatus(HttpStatus.OK)
    void addLot(@RequestBody final LotUpdateParams updateParams) {
        rusalLineItemService.addLot(updateParams);
    }

    @PostMapping("/update/lot/site")
    @ResponseStatus(HttpStatus.OK)
    void addLotSite(@RequestParam final String lot, @RequestParam final String bl, @RequestParam final String heat) {
        rusalLineItemService.addLotSite(lot, bl, heat);
    }

    @PostMapping("/update/barge")
    @ResponseStatus(HttpStatus.OK)
    void addBarge(@RequestParam final String bl, @RequestParam final String barge) {
        rusalLineItemService.addBarge(bl, barge);
    }

    @PostMapping(value = "/update/reception", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)

    void updateReception(@RequestBody final List<RusalReceptionUpdateParams> updateParams) {
        rusalLineItemService.updateReception(updateParams);
    }

}
