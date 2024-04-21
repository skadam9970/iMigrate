package com.iMigrate.core;

//import static org.hibernate.cfg.JdbcSettings.DIALECT;
//import static org.hibernate.cfg.JdbcSettings.SHOW_SQL;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;

import com.iMigrate.models.Columns;
import com.iMigrate.models.ForeignKeys;
import com.iMigrate.models.PrimaryKeys;
import com.iMigrate.models.Tables;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FetchType;
import jakarta.persistence.Query;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

/**
 * 
 * @author Habib Zerai
 *
 */
public class Main {
	public static String entityPkgPrefix =  "com.iMigrate.models.";
	public static EntityManager em = null;
	public static Map<String, Class<?>> map(List<Columns> column) {
		Map<String, Class<?>> fields = new HashMap<>();
		for (Columns columns : column) {
			if (columns.getColumnDataType().equals("BIGINT")) {
				fields.put(columns.getColumnName(), Long.class);
			} else if (columns.getColumnDataType().equals("NCHAR")) {
				fields.put(columns.getColumnName(), String.class);
			} else if (columns.getColumnDataType().equals("NUMERIC")) {
				fields.put(columns.getColumnName(), Integer.class);
			} else if (columns.getColumnDataType().equals("DATE")) {
				fields.put(columns.getColumnName(), Date.class);
			} else {
				fields.put(columns.getColumnName(), String.class);
			}
		}
		return fields;
	}

//	public static void prepareCoreEntities(Map<String, Tables> listTables) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
//		for (Map.Entry<String, Tables> entry : listTables.entrySet()) {
//			System.out.println("prepareCoreEntities, Table : "+entry.getKey());
//			Tables table = entry.getValue();
//			createJPAEntity(table);	
//		}
//		
//	}
	
	public static void createEntityManger(List<DynamicEntity> fullClassNames) {
		if(em == null) {
			em = createEMF(fullClassNames);	
			//callEmCalls();
		
		}
	}
	
	public static void callEmCalls() {
		em.getTransaction().begin();
		/*Query query = em.createNativeQuery("SELECT 1 FROM "+tables.getTableName());
		try {
			query.getSingleResult();
		} catch (Exception e) {
			// TODO: handle exception
		}*/
		
		//person = em.merge(person);
		em.flush();
		em.getTransaction().commit();
		//Class cls =Class.forName(entityPkgPrefix+ tables.getTableName());
		//System.out.print("Class "+cls+" loaded");
			//DynamicEntity beanFromDB = (DynamicEntity) em.find(Class.forName("com.iMigrate.models" + tables.getTableName()), id);
			//System.out.println(beanFromDB.get("firstName"));
		
	}

	public static DynamicEntity createJPAEntity(Tables tables)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		List<Columns> columns = tables.getColumns();
		createEntity(tables, entityPkgPrefix + tables.getTableName(), tables.getColumns());
		DynamicEntity etnity = (DynamicEntity) Class.forName(entityPkgPrefix + tables.getTableName())
				.getConstructor().newInstance();
		return etnity;
		
	}

	private static Class<?> createEntity(Tables tables, String className, List<Columns> columns)
			throws ClassNotFoundException {
		
		Map<String, Class<?>> fields = map(columns);
		var builder = new ByteBuddy().subclass(DynamicEntity.class).annotateType(
				AnnotationDescription.Builder.ofType(jakarta.persistence.Entity.class).build(),
				AnnotationDescription.Builder.ofType(jakarta.persistence.Table.class)
						.define("name", tables.getTableName()).build());
		//if ((tables.getPrimaryKeys() != null && tables.getPrimaryKeys().size() > 0)
			//	&& tables.getForeignKeys().size() == 0) {
			Map<String, PrimaryKeys> mapPK = tables.getPrimaryKeys().stream()
					.collect(Collectors.toMap(PrimaryKeys::getPrimaryKeyColName, Function.identity()));
			Map<String, ForeignKeys> fkColumns = tables.getForeignKeys().stream()
					.collect(Collectors.toMap(ForeignKeys::getFkColumnName, Function.identity()));
			for (Map.Entry<String, Class<?>> e : fields.entrySet()) {
				// Skip primarykey column
				//Columns idColumn = mapColumns.get(e.getKey());
				if (mapPK.containsKey(e.getKey())) {
					continue;
				} else if (fkColumns.containsKey(e.getKey())) {
					ForeignKeys fks = fkColumns.get(e.getKey());
					// @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, optional =
					// false)
					// @JoinColumn(name="TCONC_CONTRACT_ID_SK", referencedColumnName =
					// "pkColumnName", nullable=false, updatable = false)
					// private Department department;
					Class clazz = Class.forName(entityPkgPrefix + fks.getPkTableName());
					builder = builder.defineField(fks.getPkTableName(), clazz).annotateField(
							AnnotationDescription.Builder.ofType(jakarta.persistence.JoinColumn.class)
									.define("name", fks.getFkColumnName())
									.define("referencedColumnName", fks.getPkColumnName()).build(),

							AnnotationDescription.Builder.ofType(jakarta.persistence.ManyToOne.class)
									.define("fetch", FetchType.EAGER)
									// .define("cascade", CascadeType.REFRESH)
									.define("optional", false).build()
									);
				} else {
					builder = builder.defineField(e.getKey(), e.getValue())
							.annotateField(AnnotationDescription.Builder.ofType(jakarta.persistence.Column.class).build());
				}
				
			}
			if (tables.getPrimaryKeys().size() > 0) {
				PrimaryKeys primaryKeys = tables.getPrimaryKeys().get(0);
				Class clazz = fields.get(primaryKeys.getPrimaryKeyColName());
				builder = builder.defineField(primaryKeys.getPrimaryKeyColName(), clazz).annotateField(
						AnnotationDescription.Builder.ofType(jakarta.persistence.Id.class).build(),
						AnnotationDescription.Builder.ofType(jakarta.persistence.Column.class).build(),
						AnnotationDescription.Builder.ofType(jakarta.persistence.GeneratedValue.class).build());
			}
			//Load the entity
			Unloaded<?> generatedClass = builder.name(className).make();
			generatedClass.load(Main.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION);

		/*}

		if (tables.getForeignKeys() != null && tables.getForeignKeys().size() > 0) {
			Map<String, ForeignKeys> fkColumns = tables.getForeignKeys().stream()
					.collect(Collectors.toMap(ForeignKeys::getFkColumnName, Function.identity()));
			for (Map.Entry<String, Class<?>> e : fields.entrySet()) {

				if (fkColumns.containsKey(e.getKey())) {
					

				} else {
					builder = builder.defineField(e.getKey(), e.getValue()).annotateField(
							AnnotationDescription.Builder.ofType(jakarta.persistence.Column.class).build());

				}

			}

			if (tables.getPrimaryKeys() != null && tables.getPrimaryKeys().size() > 0) {
				PrimaryKeys primaryKeys = tables.getPrimaryKeys().get(0);
				Class clazz = fields.get(primaryKeys.getPrimaryKeyColName());
				builder = builder.defineField(primaryKeys.getPrimaryKeyColName(), clazz).annotateField(
						AnnotationDescription.Builder.ofType(jakarta.persistence.Id.class).build(),
						AnnotationDescription.Builder.ofType(jakarta.persistence.Column.class).build(),
						AnnotationDescription.Builder.ofType(jakarta.persistence.GeneratedValue.class).build());
				
			}
			Unloaded<?> generatedClass = builder.name(className).make();
			generatedClass.load(Main.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION);

		}*/
		try {
			Class<?> cls = Class.forName(className);
			return cls;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static EntityManager createEMF(Object... entities) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
		properties.put("hibernate.connection.username", "postgres");
		properties.put("hibernate.connection.password", "postgres");
		properties.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/iMigratedb");
		properties.put("hibernate.hbm2ddl.auto", "create");
		properties.put("hibernate.show_sql", true);
		EntityManagerFactory entityManagerFactory = new HibernatePersistenceProvider()
				.createContainerEntityManagerFactory(dynamicjpa(entities), properties);
		return entityManagerFactory.createEntityManager();

	}

	private static PersistenceUnitInfo dynamicjpa(Object... entities) {
		return new PersistenceUnitInfo() {
			@Override
			public String getPersistenceUnitName() {
				return "dynamic-jpa";
			}

			@Override
			public List<String> getManagedClassNames() {
				List<String> list = new ArrayList<>();
				for (Object entity : entities) {
					if(entity.getClass().getName().equals("java.util.ArrayList")) {
						ArrayList<Object> listArr = (ArrayList<Object>)entity;
						for (Object object : listArr) {
							list.add(object.getClass().getName());
						}
					}else {
						list.add(entity.getClass().getName());
					}
					
				}
				return list;
			}

			@Override
			public String getPersistenceProviderClassName() {
				return "org.hibernate.jpa.HibernatePersistenceProvider";
			}

			@Override
			public PersistenceUnitTransactionType getTransactionType() {
				return PersistenceUnitTransactionType.RESOURCE_LOCAL;
			}

			@Override
			public DataSource getJtaDataSource() {
				return null;
			}

			@Override
			public DataSource getNonJtaDataSource() {
				return null;
			}

			@Override
			public List<String> getMappingFileNames() {
				return Collections.emptyList();
			}

			@Override
			public List<URL> getJarFileUrls() {
				try {
					return Collections.list(this.getClass().getClassLoader().getResources(""));
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}

			@Override
			public URL getPersistenceUnitRootUrl() {
				return null;
			}

			@Override
			public boolean excludeUnlistedClasses() {
				return false;
			}

			@Override
			public SharedCacheMode getSharedCacheMode() {
				return null;
			}

			@Override
			public ValidationMode getValidationMode() {
				return null;
			}

			@Override
			public Properties getProperties() {
				return new Properties();
			}

			@Override
			public String getPersistenceXMLSchemaVersion() {
				return null;
			}

			@Override
			public ClassLoader getClassLoader() {
				return null;
			}

			@Override
			public void addTransformer(ClassTransformer transformer) {

			}

			@Override
			public ClassLoader getNewTempClassLoader() {
				return null;
			}
		};
	}

	public static class DynamicEntity {

		public void set(String key, Object value) {
			try {
				java.lang.reflect.Field field = this.getClass().getDeclaredField(key);
				field.setAccessible(true);
				field.set(this, value);
				field.setAccessible(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public Object get(String key) {
			try {
				java.lang.reflect.Field field = this.getClass().getDeclaredField(key);
				field.setAccessible(true);
				Object value = field.get(this);
				field.setAccessible(false);
				return value;
			} catch (Exception e) {
			}
			return null;
		}

	}

}
