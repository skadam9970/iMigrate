package com.entities;

public enum DataTypeMapping {
	 
	
	BIGINT("BIGINT", "BIGINT"), 
	VARCHAR("VARCHAR", "VARCHAR"), 
	BINARY("BINARY(n)", "BYTEA"),
	BIT("BIT)", "BOOLEAN"),
	CHAR("CHAR(n)", "CHAR(n)"),
	CHARACTER("CHARACTER(n)", "CHARACTER(n)"), 
	DATE("DATE", "DATE"),
	DATETIME("DATETIME", "TIMESTAMP(3)"), 
	DATETIME2("DATETIME2(p)", "TIMESTAMP(p)"),
	DATETIMEOFFSET("DATETIMEOFFSET(p)", "TIMESTAMP(p)"), 
	DECIMAL("DECIMAL(p,s)", "DECIMAL(p,s)"),
	DEC("DEC(p,s)", "DEC(p,s))"), 
	DOUBLEPRECISION("DOUBLE PRECISION", "DOUBLE PRECISION"),
	FLOAT("FLOAT(p)", "FLOAT(p)"), 
	IMAGE("IMAGE", "BYTEA"), 
	INT("INT", "INTEGER"),
	INTEGER("INTEGER", "INTEGER"), 
	MONEY("MONEY", "MONEY"),
	NCHAR("NCHAR(n)", "CHAR(n)"), 
	NTEXT("NTEXT", "TEXT"), 
	NUMERIC("NUMERIC(p,s)", "NUMERIC(p,s)"),
	NVARCHAR("NVARCHAR(n)", "VARCHAR(n)"), 
	NVARCHAR_MAX("NVARCHAR(max)", "TEXT"), 
	REAL("REAL", "REAL"),
	ROWVERSION("ROWVERSION", "BYTEA"), 
	SMALLDATETIME("SMALLDATETIME", "TIMESTAMP(0)"), 
	SMALLINT("SMALLINT", "SMALLINT"),
	SMALLMONEY("SMALLMONEY", "MONEY"), 
	TEXT("TEXT", "TEXT"), 
	TIME("TIME(p)", "TIME(p)"),
	TIMESTAMP("TIMESTAMP", "BYTEA"), 
	TINYINT("TINYINT", "SMALLINT"), 
	UNIQUEIDENTIFIER("UNIQUEIDENTIFIER", "UUID"),
	VARBINARY("VARBINARY(n)", "BYTEA"), 
	VARBINARY_MAX("VARBINARY(max)", "BYTEA"), 
	VARCHAR_MAX("VARCHAR(max)", "TEXT"),
	XML("XML", "XML")
	;

	private final String sqlServerType;
	private final String postgreSQLType;

	DataTypeMapping(String sqlServerType, String postgreSQLType) {
		this.sqlServerType = sqlServerType;
		this.postgreSQLType = postgreSQLType;
	}

	public String getSqlServerType() {
		return sqlServerType;
	}

	public String getPostgreSQLType() {
		return postgreSQLType;
	}

	// Helper method to get PostgreSQL type from SQL Server type
	public static String getPostgreSQLTypeFromSQLServer(String sqlServerType) {
		for (DataTypeMapping mapping : values()) {
			if (mapping.sqlServerType.equalsIgnoreCase(sqlServerType)) {
				return mapping.postgreSQLType;
			}
		}
		return null; // Or throw exception if needed
	}
}
