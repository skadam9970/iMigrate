package com.ltimindtree.imigrate.sourcedb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

@Service
public class GetMetaData {

	@Autowired
	private DataSource dataSource;
	
	private static Logger log = LoggerFactory.getLogger(GetMetaData.class);
	
	public void printMetaData() {
		
		Metadata metadata = MetadataExtractorIntegrator.INSTANCE.getMetadata();
		Database db = MetadataExtractorIntegrator.INSTANCE.getDatabase();

		
		
		System.out.println(db.getDefaultNamespace().getName());
		
		for(Namespace ns :db.getNamespaces()) {
			log.info("NameSpace: {}",ns.getName());
			Collection<Table> col = ns.getTables();
			for(Table table: col) {
				log.info("Table Name: {}",table.getName());
				Collection<Column> columns = table.getColumns();
				for(Column colm:columns) {
					log.info("Column name: {}",colm.getName());
					
				}
			}
		}
		
		
		for ( PersistentClass persistentClass : metadata.getEntityBindings()) {

			Table table = persistentClass.getTable();

			log.info( "Entity: {} is mapped to table: {}",
					persistentClass.getClassName(),
					table.getName()
			);
		}

	}


	public void printTableData(){

        try {
			Connection connection = dataSource.getConnection();
			DatabaseMetaData metaData = connection.getMetaData();

			ResultSet resultSet = metaData.getTables(null, null, null, new String[]{"TABLE","VIEW", "GLOBAL TEMPORARY","LOCAL TEMPORARY", "ALIAS", "SYNONYM"});
			while(resultSet.next()){
				String tableName = resultSet.getString("TABLE_NAME");
				String tableCatalog = resultSet.getString("TABLE_CAT");
				String tableSchema = resultSet.getString("TABLE_SCHEM");
				String tableType = resultSet.getString("TABLE_TYPE");

				System.out.println("Catalog: " + tableCatalog);
				System.out.println("Schema: " + tableSchema);
				System.out.println("Table Type: " + tableType);

				System.out.println("Table Name: " +  tableName);

				ResultSet columns = metaData.getColumns(null, null, tableName, null);

				while(columns.next()){
					String columnName = columns.getString("COLUMN_NAME");
					System.out.println("Column Name: " + columnName);
					String columnSize = columns.getString("COLUMN_SIZE");
					System.out.println("Column Size: " + columnSize);
					String columnDataType = columns.getString("DATA_TYPE");
					System.out.println("Column Data Type: " + columnDataType);
					String isNullable = columns.getString("IS_NULLABLE");
					System.out.println("Column Is Nullable: " + isNullable);
					String isAutoIncrement = columns.getString("IS_AUTOINCREMENT");
					System.out.println("Column Is auto Increment: " + isAutoIncrement);
				}
				ResultSet primaryKeys = metaData.getPrimaryKeys(tableCatalog, tableSchema, tableName);
				while(primaryKeys.next()){
					String primaryKeyColName = primaryKeys.getString("COLUMN_NAME");
					System.out.println("Primary Key Column Name: " + primaryKeyColName);
					String primaryKeyName = primaryKeys.getString("PK_NAME");
					System.out.println("Primary Key Name: " + primaryKeyName);
				}
				ResultSet foreignKeys = metaData.getImportedKeys(tableCatalog, tableSchema, tableName);
				while(foreignKeys.next()){
					String pkTableName = foreignKeys.getString("PKTABLE_NAME");
					System.out.println("Primary Key Table Name: " + pkTableName);
					String fkTableName = foreignKeys.getString("FKTABLE_NAME");
					System.out.println("Foreign Key Table Name: " + fkTableName);
					String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
					System.out.println("Primary Key Table Column Name: " + pkColumnName);
					String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
					System.out.println("Foreign Key Table Column Name: " + fkColumnName);
				}

				ResultSet indexInfo = metaData.getIndexInfo(tableCatalog, tableSchema, tableName, false, true);
				while(indexInfo.next()){
					String indexName = indexInfo.getString("INDEX_NAME");
					String type = indexInfo.getString("TYPE");
					System.out.println("Index Name: "+indexName);
					System.out.println("Index Type: "+type);
				}


			}


		} catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
