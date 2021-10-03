package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RusalLineItemRepository extends JpaRepository<RusalLineItem, String> {

    @Query("SELECT * FROM current_inventory WHERE work_order = :workOrder AND load_num = :loadNum")
    List<RusalLineItem> findByOrderAndLoad(String workOrder, String loadNum);
}
