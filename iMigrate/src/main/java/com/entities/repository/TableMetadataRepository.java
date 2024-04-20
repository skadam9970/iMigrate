package com.entities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entities.TableMetadata;
import com.entities.TableMetadata.TableMetadataKey;

@Repository
public interface TableMetadataRepository extends JpaRepository<TableMetadata, TableMetadataKey> {
	
	TableMetadata findByTableName(String tableName);
	
}
