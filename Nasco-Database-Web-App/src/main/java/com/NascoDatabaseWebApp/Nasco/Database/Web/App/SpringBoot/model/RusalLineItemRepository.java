package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RusalLineItemRepository extends JpaRepository<RusalLineItem, String> {

    @Query("SELECT * FROM current_inventory WHERE work_order = :searchOrder AND load_num = :searchLoad")
    List<RusalLineItem> findByOrderAndLoad(@Param("searchOrder") String workOrder, @Param("searchLoad") String loadNum);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE current_inventory SET work_order = :reqOrder AND load_num = :reqLoadNum AND loader = :reqLoader AND load_time = :reqLoadTime WHERE heat_num =: searchHeat")
    void update(@Param("searchHeat") String heatNum, @Param("reqOrder") String workOrder, @Param("reqLoadNum") String loadNum, @Param("reqLoader") String loader, @Param("reqLoadTime") String loadTime);
}
