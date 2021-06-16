package com.springboot.inventory.dto.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.inventory.dto.InventoryTable;

@Repository
@Transactional
public interface InventoryRepository extends JpaRepository<InventoryTable, String> {


	//InventoryTable findByItemIdContainingIgnoreCase(String itemId);
	


}
