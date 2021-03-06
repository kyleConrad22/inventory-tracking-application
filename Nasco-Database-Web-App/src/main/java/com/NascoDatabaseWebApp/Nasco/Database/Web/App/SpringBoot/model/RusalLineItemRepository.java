package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RusalLineItemRepository extends JpaRepository<RusalLineItem, String> {

    @Query(
        value = "SELECT * FROM current_inventory WHERE work_order = :searchOrder AND load_num = :searchLoad",
        nativeQuery = true
    )
    List<RusalLineItem> findByOrderAndLoad(@Param("searchOrder") String workOrder, @Param("searchLoad") String loadNum);

    @Query(
            value ="SELECT * FROM current_inventory WHERE barge = :searchBarge",
            nativeQuery = true
    )
    List<RusalLineItem> findByBarge(@Param("searchBarge") String barge);

    /* TODO - Update such that it uses reception date to reset lot numbers at beginning of each year / returns reception data as well*/
    @Query(
            value = "SELECT DISTINCT lot FROM current_inventory",
            nativeQuery = true
    )
    List<String> getUniqueLots();

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
        value = "UPDATE current_inventory SET work_order = :reqOrder, load_num = :reqLoadNum, loader = :reqLoader, load_time = :reqLoadTime WHERE heat_num = :searchHeat",
        nativeQuery = true
    )
    void updateShipment(@Param("searchHeat") String heatNum, @Param("reqOrder") String workOrder, @Param("reqLoadNum") String loadNum, @Param("reqLoader") String loader, @Param("reqLoadTime") String loadTime);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
        value = "UPDATE current_inventory SET mark = :reqMark WHERE bl_num = :searchBl",
        nativeQuery = true
    )
    void addMark(@Param("searchBl") String bl, @Param("reqMark") String mark);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
        value = "UPDATE current_inventory SET barge = :reqBarge WHERE bl_num = :searchBl",
        nativeQuery = true
    )
    void addBarge(@Param("searchBl") String bl, @Param("reqBarge") String barge);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
        value = "UPDATE current_inventory SET reception_date = :reqReceptionDate, checker = :reqChecker WHERE heat_num = :searchHeat",
        nativeQuery = true
    )
    void updateReception(@Param("searchHeat") String heat, @Param("reqReceptionDate") String receptionDate, @Param("reqChecker") String checker);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
        value = "UPDATE current_inventory SET lot = :reqLot WHERE bl_num = :searchBl AND heat_num LIKE CONCAT('%', :searchHeat, '%')",
        nativeQuery = true
    )
    void addLot(@Param("reqLot") String lot, @Param("searchBl") String bl, @Param("searchHeat") String heat);

    @Query(
        value = "SELECT * FROM current_inventory WHERE NULLIF(reception_date, '') IS NOT NULL",
        nativeQuery = true
    )
    List<RusalLineItem> findReceivedItems();

    @Query(
        value = "SELECT COUNT(reception_date) FROM current_inventory WHERE barge = :searchBarge",
        nativeQuery = true
    )
    int countIncomingItems(@Param("searchBarge") String barge);

    @Query(
        value = "SELECT COUNT(reception_date) FROM current_inventory WHERE barge = :searchBarge AND NULLIF(reception_date, '') IS NOT NULL",
        nativeQuery = true
    )
    int countReceivedItems(@Param("searchBarge") String barge);
}
