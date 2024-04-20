package com.entities;

import java.io.Serializable;

import com.entities.TableMetadata.TableMetadataKey;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@IdClass(TableMetadataKey.class)
@Table(name = "table_metadata")
@Data
public class TableMetadata {
	@Column(name = "column_name")
	@Id
	String columnName;

	@Column(name = "table_name")
	@Id
	String tableName;
	
	

	public static class TableMetadataKey implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4116060603725422581L;
		String columnName;
		String tableName;
	}
}