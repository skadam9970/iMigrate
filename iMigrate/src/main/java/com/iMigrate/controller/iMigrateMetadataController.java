package com.iMigrate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iMigrate.core.Main;
import com.service.GetMetaData;

@RestController
@RequestMapping("/metadata")
public class iMigrateMetadataController {
	
//	@Autowired
//	public TableMetadataRepository repo;
//	
	@Autowired
	public GetMetaData metaData;

    @GetMapping("/tables")
    public ResponseEntity getMetaData() throws Exception {
        try {
//           List<TableMetadata> list =  repo.findAll();
//           for(TableMetadata data :  list) {
//        	  // data.getTableName()
//           }
        	metaData.printMetaData();
        	metaData.printTableData();
        	metaData.getTriggers();
//        	Main.main1();
        } catch (Exception e) {
           e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
