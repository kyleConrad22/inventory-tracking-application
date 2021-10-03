package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RusalLineItemRepository extends JpaRepository<RusalLineItem, String> {

    @Query(
        value = "SELECT * FROM current_inventory WHERE work_order = :searchOrder AND load_num = :searchLoad",
        nativeQuery = true
    )
    List<RusalLineItem> findByOrderAndLoad(@Param("searchOrder") String workOrder, @Param("searchLoad") String loadNum);

    @Modifying(clearAutomatically = true)
    @Query(
        value = "UPDATE current_inventory SET work_order = :reqOrder, load_num = :reqLoadNum, loader = :reqLoader, load_time = :reqLoadTime WHERE heat_num = :searchHeat",
        nativeQuery = true
    )
    void update(@Param("searchHeat") String heatNum, @Param("reqOrder") String workOrder, @Param("reqLoadNum") String loadNum, @Param("reqLoader") String loader, @Param("reqLoadTime") String loadTime);
}
