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
	public static Map<String, Class<?>> map(List<Columns> column) {
		Map<String, Class<?>> fields = new HashMap<>();
		//TODO MSSQL to PostgreSQL table columns
		fields.put("firstName", String.class);
		fields.put("lastName", String.class);
		fields.put("birthDate", Date.class);
		fields.put("image", byte[].class);
		fields.put("version", Long.class);

		return fields;
	}
	public static void main1(Tables tables)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		List<Columns> columns = tables.getColumns();
		createEntity(tables, "com.iMigrate.models"+tables.getTableName(), map(columns));
		DynamicEntity person = (DynamicEntity) Class.forName("com.iMigrate.models"+tables.getTableName()).getConstructor()
				.newInstance();
//		person.set("firstName", "Habib");
//		person.set("lastName", "Zerai");
//		person.set("birthDate", new Date());
//		person.set("image", new byte[] { 1, 2, 3 });
//		person.set("version", 0L);

		EntityManager em = createEMF(person);
		em.getTransaction().begin();
		person = em.merge(person);
		em.flush();
		em.getTransaction().commit();
//		Long id = (Long) person.get("id");
//
//		DynamicEntity beanFromDB = (DynamicEntity) em.find(Class.forName("com.hzerai.dynamicjpa.models.Person"), id);
//		//System.out.println(beanFromDB.get("firstName"));
	}

	private static Class<?> createEntity(Tables tables, String className, Map<String, Class<?>> fields) throws ClassNotFoundException {

		var builder = new ByteBuddy().subclass(DynamicEntity.class)
				.annotateType(AnnotationDescription.Builder.ofType(jakarta.persistence.Entity.class).build(),
						AnnotationDescription.Builder.ofType(jakarta.persistence.Table.class).define("name", tables.getTableName()) 
						.build());
		if(tables.getPrimaryKeys().size() > 0 && tables.getForeignKeys().size() == 0) {
			for (Map.Entry<String, Class<?>> e : fields.entrySet()) {
				builder = builder.defineField(e.getKey(), e.getValue())
						.annotateField(AnnotationDescription.Builder.ofType(jakarta.persistence.Column.class).build());
			}
			PrimaryKeys primaryKeys = tables.getPrimaryKeys().get(0);
			Class clazz = fields.get(primaryKeys.getPrimaryKeyColName());
			builder = builder.defineField(primaryKeys.getPrimaryKeyColName(), clazz).annotateField(
					AnnotationDescription.Builder.ofType(jakarta.persistence.Id.class).build(),
					AnnotationDescription.Builder.ofType(jakarta.persistence.Column.class).build(),
					AnnotationDescription.Builder.ofType(jakarta.persistence.GeneratedValue.class).build());
			Unloaded<?> generatedClass = builder.name(className).make();
			generatedClass.load(Main.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION);
			

		}
		
		if(tables.getForeignKeys().size() > 0) {
			Map<String, ForeignKeys> fkColumns = tables.getForeignKeys().stream()
					.collect(Collectors 
	                              .toMap(ForeignKeys::getFkColumnName, Function.identity())); 
			for (Map.Entry<String, Class<?>> e : fields.entrySet()) {
				
				if(fkColumns.containsKey(e.getKey())) {
					ForeignKeys fks = fkColumns.get(e.getKey());
					//@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH, optional = false)
				    //@JoinColumn(name="TCONC_CONTRACT_ID_SK", referencedColumnName = "pkColumnName", nullable=false, updatable = false)
					//private Department department;
					Class clazz = Class.forName("com.iMigrate.models"+fks.getPkTableName());
					builder = builder.defineField(fks.getPkTableName(), clazz)
							.annotateField(AnnotationDescription.Builder.ofType(jakarta.persistence.JoinColumns.class)
									.define("name", fks.getPkColumnName())
									.define("referencedColumnName", fks.getFkColumnName())
									.build());
				} else {
					builder = builder.defineField(e.getKey(), e.getValue())
							.annotateField(AnnotationDescription.Builder.ofType(jakarta.persistence.Column.class).build());
						
				}
				
			}
		
		
		
			PrimaryKeys primaryKeys = tables.getPrimaryKeys().get(0);
			Class clazz = fields.get(primaryKeys.getPrimaryKeyColName());
			builder = builder.defineField(primaryKeys.getPrimaryKeyColName(), clazz).annotateField(
					AnnotationDescription.Builder.ofType(jakarta.persistence.Id.class).build(),
					AnnotationDescription.Builder.ofType(jakarta.persistence.Column.class).build(),
					AnnotationDescription.Builder.ofType(jakarta.persistence.GeneratedValue.class).build());
			Unloaded<?> generatedClass = builder.name(className).make();
			generatedClass.load(Main.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION);
			

		}
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
					list.add(entity.getClass().getName());
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
