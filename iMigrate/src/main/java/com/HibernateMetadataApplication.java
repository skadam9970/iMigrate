package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.entities.DataTypeMapping;
import com.entities.HibernateMetadataApplicationTests;

@SpringBootApplication
public class HibernateMetadataApplication {

	public static void main(String[] args) {
		SpringApplication.run(HibernateMetadataApplication.class, args);
//		HibernateMetadataApplicationTests obj = new HibernateMetadataApplicationTests();
		// obj.contextLoads();
		String sqlServerType = "DOUBLE PRECISION";

		// Get PostgreSQL type from SQL Server type
		String postgreSQLType = DataTypeMapping.getPostgreSQLTypeFromSQLServer(sqlServerType);

		if (postgreSQLType != null) {
			System.out.println("SQL Server type: " + sqlServerType);
			System.out.println("PostgreSQL type: " + postgreSQLType);
		} else {
			System.out.println("Mapping not found for SQL Server type: " + sqlServerType);
		}
	}
}
