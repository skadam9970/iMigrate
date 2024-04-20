package com.entities;

import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.integrators.MetadataExtractorIntegrator;

@Component
public class HibernateMetadataApplicationTests {

	private static Logger LOGGER = LoggerFactory.getLogger(HibernateMetadataApplicationTests.class);

	
	public void contextLoads() {
		//DatabaseMetaData s = null;
		//TableMetadata a;
		//TableMetadataKey b;
		Metadata metadata = MetadataExtractorIntegrator.INSTANCE.getMetadata();

		for ( PersistentClass persistentClass : metadata.getEntityBindings()) {

			Table table = persistentClass.getTable();

			LOGGER.info( "Entity: {} is mapped to table: {}",
					persistentClass.getClassName(),
					table.getName()
			);

			for(Property property: persistentClass.getProperties()){
				for(Column column : property.getColumns()) {
					LOGGER.info( "Property: {} is mapped on table column: {} of type: {}",
							property.getName(),
							column.getName(),
							column.getSqlType()
					);
				}
			}
		}
	}

}
