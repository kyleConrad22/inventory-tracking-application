package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RusalLineItemRepository extends JpaRepository<RusalLineItem, String> {
    List<RusalLineItem> findByWorkOrder(String workOrder);

    RusalLineItem findByHeatNum(String heatNum);
}
