package com.service;


import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iMigrate.core.Main;
import com.iMigrate.models.Columns;
import com.iMigrate.models.ForeignKeys;
import com.iMigrate.models.Indexes;
import com.iMigrate.models.PrimaryKeys;
import com.iMigrate.models.Tables;
import com.integrators.MetadataExtractorIntegrator;

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

			//ResultSet resultSet = metaData.getTables(null, null, null, new String[]{"TABLE","VIEW", "GLOBAL TEMPORARY","LOCAL TEMPORARY", "ALIAS", "SYNONYM"});
			ResultSet resultSet = metaData.getTables(null, "dbo", null, new String[]{"TABLE"});
			Map<String, Tables> listtables = new LinkedHashMap();
			while(resultSet.next()){
				Tables tables = Tables.builder().tableName(resultSet.getString("TABLE_NAME"))
				.tableCatalog(resultSet.getString("TABLE_CAT"))
				.tableSchema(resultSet.getString("TABLE_SCHEM"))
				.tableType(resultSet.getString("TABLE_TYPE")).build();
				if(tables.getTableName().equals("table_metadata")) {
					continue;
				}
//				System.out.println("Catalog: " + tableCatalog  + ", Schema: " + tableSchema +"Table Name: " +  tableName + " Table Type: " + tableType);

				ResultSet columnsRS = metaData.getColumns(null, null, tables.getTableName(), null);
				List<Columns> lisCols= new ArrayList<>();
				while(columnsRS.next()){
					//Columns columns;
					Columns columns = Columns.builder().columnName(columnsRS.getString("COLUMN_NAME"))
					.columnSize(columnsRS.getString("COLUMN_SIZE"))
					.columnDataType(JDBCType.valueOf(Integer.parseInt(columnsRS.getString("DATA_TYPE"))).getName())
					.isNullable(columnsRS.getString("IS_NULLABLE"))
					.isAutoIncrement(columnsRS.getString("IS_AUTOINCREMENT")).build();
					lisCols.add(columns);
					
					//System.out.println("Column Name: " + columnName +", Column Size: " + columnSize+", Column Data Type: " + columnDataType +", Column Is Nullable: " + isNullable +", Column Is auto Increment: " + isAutoIncrement);
				}
				tables = tables.toBuilder().columns(lisCols).build();
				////////////////////////////////////////////////
				ResultSet primaryKeys = metaData.getPrimaryKeys(tables.getTableCatalog(), tables.getTableSchema(), tables.getTableName());
				List<PrimaryKeys> listPKs= new ArrayList<>();
				while(primaryKeys.next()){
					PrimaryKeys pks = PrimaryKeys.builder().primaryKeyColName(primaryKeys.getString("COLUMN_NAME"))
					.primaryKeyName(primaryKeys.getString("PK_NAME")).build();
					listPKs.add(pks);
					//System.out.println("Primary Key Column Name: " + primaryKeyColName+", Primary Key Name: " + primaryKeyName);
				}
				tables = tables.toBuilder().primaryKeys(listPKs).build();
				/////////////////////////////////////////////////
				ResultSet foreignKeys = metaData.getImportedKeys(tables.getTableCatalog(), tables.getTableSchema(), tables.getTableName());
				List<ForeignKeys> listFKs= new ArrayList<>();
				while(foreignKeys.next()){
					ForeignKeys fks= ForeignKeys.builder()
					.pkTableName(foreignKeys.getString("PKTABLE_NAME"))
					.fkTableName(foreignKeys.getString("FKTABLE_NAME"))
					.pkColumnName(foreignKeys.getString("PKCOLUMN_NAME"))
					.fkColumnName(foreignKeys.getString("FKCOLUMN_NAME")).build();
					listFKs.add(fks);
					
				//	System.out.println("Primary Key Table Name: " + pkTableName+", Foreign Key Table Name: " + fkTableName + "Primary Key Table Column Name: " + pkColumnName+ ", Foreign Key Table Column Name: " + fkColumnName);
				}
				tables = tables.toBuilder().foreignKeys(listFKs).build();
				
				//////////////////////////////////////////////////////
				ResultSet indexInfo = metaData.getIndexInfo(tables.getTableCatalog(), tables.getTableSchema(), tables.getTableName(), false, true);
				List<Indexes> listInds= new ArrayList<>();
				while(indexInfo.next()){
					Indexes idx = Indexes.builder()
					.indexName(indexInfo.getString("INDEX_NAME"))
					.type(indexInfo.getString("TYPE")).build();
					listInds.add(idx);
					//System.out.println("Index Name: "+indexName);
					//System.out.println("Index Type: "+type);
				}
				tables =  tables.toBuilder().indexes(listInds).build();
				listtables.put(tables.getTableName(), tables);
			}
			System.out.println(listtables);
			for (Map.Entry<String, Tables> entry : listtables.entrySet()) {
				Tables tables= entry.getValue();
				if(tables.getForeignKeys().size()>0) {
					List<Tables> partentTables = new ArrayList<>();
					for (ForeignKeys fks : tables.getForeignKeys()) {
						partentTables.add(listtables.get(fks.getPkTableName()));
					}
					tables.toBuilder().parentKeyTables(partentTables).build();
				}
				
			}
			
			List<String> doneTables = new ArrayList<>();
			for (Map.Entry<String, Tables> entry : listtables.entrySet()) {
				String key = entry.getKey();
				Tables tables= entry.getValue();
				if(doneTables.contains(key)){
					continue;
				}
				//Call object creation
			}

		} catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
	
	public void prepateEntity(Map<String, Tables> listtables, List<String> doneTables) {
		for (Map.Entry<String, Tables> entry : listtables.entrySet()) {
			Tables tables= entry.getValue();
			createEntity(tables, doneTables);
		}
	}
	
	public void createEntity(Tables tables, List<String> doneTables) {		
		if(doneTables.contains(tables.getTableName())){
			return;
		} else if(tables.getParentKeyTables().size()>0) {
			for(Tables parentTable : tables.getParentKeyTables()) {
				createEntity(parentTable, doneTables);
			}
		}
		//Call object creation
		doneTables.add(tables.getTableName());
	}
//Employee1
//Employee -> Department
//Department
	
	public void createJPAEntity(Tables tables) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		Main.main1(tables);
	}
}
