package com.dnd.ground.global.config;

import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.spatial.dialect.mysql.MySQL8SpatialDialect;
import org.hibernate.type.BooleanType;
import org.hibernate.type.StandardBasicTypes;

/**
 * @description QueryDSL에서 MySQL 함수를 호출하기 위해 함수 등록
 * @author 박찬호
 * @since 2023-02-18
 * @updated 1. Spatial 관련 함수 등록
 *          - 2023.02.18 박찬호
 */
public class MySQL8Dialect extends MySQL8SpatialDialect {
    public MySQL8Dialect() {
        super();
        registerFunction("ST_X", new StandardSQLFunction("ST_X", StandardBasicTypes.DOUBLE));
        registerFunction("ST_Y", new StandardSQLFunction("ST_Y", StandardBasicTypes.DOUBLE));
        registerFunction("MBRContains", new SQLFunctionTemplate(BooleanType.INSTANCE, "MBRContains(ST_LINESTRINGFROMTEXT(?1), ?2)"));
    }
}