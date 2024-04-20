package com.iMigrate.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iMigrate.entity.DynamicEntity;
import com.iMigrate.models.Columns;
import com.iMigrate.models.ForeignKeys;
import com.iMigrate.models.PrimaryKeys;
import com.iMigrate.models.Tables;
import com.iMigrate.service.IMigrateEntityCreationService;
import com.iMigrate.util.IMigrateUtils;

import jakarta.persistence.FetchType;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

/**
 * 
 * @author
 *
 */
@Service
public class IMigrateEntityCreationServiceImpl implements IMigrateEntityCreationService {

	public static String entityPkgPrefix = "com.iMigrate.models.";

	@Autowired
	private IMigrateUtils IMigrateUtils;

	/**
	 * @param tables
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	@Override
	public DynamicEntity getJPAEntity(Tables tables)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {

		createJPAEntity(tables, entityPkgPrefix + tables.getTableName(), tables.getColumns());
		DynamicEntity entity = (DynamicEntity) Class.forName(entityPkgPrefix + tables.getTableName()).getConstructor()
				.newInstance();

		return entity;
	}

	/**
	 * @param tables
	 * @param className
	 * @param columns
	 * @return
	 * @throws ClassNotFoundException
	 */
	private Class<?> createJPAEntity(Tables tables, String className, List<Columns> columns)
			throws ClassNotFoundException {

		Map<String, Class<?>> fields = IMigrateUtils.mapColumnDataType(columns);

		var builder = new ByteBuddy().subclass(DynamicEntity.class).annotateType(
				AnnotationDescription.Builder.ofType(jakarta.persistence.Entity.class).build(),
				AnnotationDescription.Builder.ofType(jakarta.persistence.Table.class)
						.define("name", tables.getTableName()).build());

		Map<String, PrimaryKeys> pkMap = tables.getPrimaryKeys().stream()
				.collect(Collectors.toMap(PrimaryKeys::getPrimaryKeyColName, Function.identity()));

		Map<String, ForeignKeys> fkColsMap = tables.getForeignKeys().stream()
				.collect(Collectors.toMap(ForeignKeys::getFkColumnName, Function.identity()));

		for (Map.Entry<String, Class<?>> e : fields.entrySet()) {
			if (pkMap.containsKey(e.getKey())) {
				continue;
			} else if (fkColsMap.containsKey(e.getKey())) {
				ForeignKeys fks = fkColsMap.get(e.getKey());
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
								.define("optional", false).build());
			} else {
				builder = builder.defineField(e.getKey(), e.getValue())
						.annotateField(AnnotationDescription.Builder.ofType(jakarta.persistence.Column.class).build());
			}
		}

		if (tables.getPrimaryKeys().size() > 0) {
			PrimaryKeys pks = tables.getPrimaryKeys().get(0);
			Class clazz = fields.get(pks.getPrimaryKeyColName());
			builder = builder.defineField(pks.getPrimaryKeyColName(), clazz).annotateField(
					AnnotationDescription.Builder.ofType(jakarta.persistence.Id.class).build(),
					AnnotationDescription.Builder.ofType(jakarta.persistence.Column.class).build(),
					AnnotationDescription.Builder.ofType(jakarta.persistence.GeneratedValue.class).build());
		}
		// Load the entity
		Unloaded<?> generatedClass = builder.name(className).make();
		generatedClass.load(IMigrateEntityCreationServiceImpl.class.getClassLoader(),
				ClassLoadingStrategy.Default.INJECTION);

		try {
			Class<?> cls = Class.forName(className);
			return cls;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
