package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model;

import com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot.model.RusalLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RusalLineItemRepository extends CrudRepository<RusalLineItem, String> {
}
