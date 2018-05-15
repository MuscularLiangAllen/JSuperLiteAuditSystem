package com.liangtee.jsuperlite.auditsys.service.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Allen on 2017/4/17.
 */

@Component("queryHelper")
public class QueryHelper {

    private static final String SIMPLE_SELECT_SQL = "SELECT * FROM %s WHERE %s";

    private static final String SELECT_SQL_LIMIT = "SELECT * FROM %s WHERE %s LIMIT %d, %d";

    private static final String SELECT_SQL_ORDER = "SELECT * FROM %s WHERE %s ORDER BY %s %s";

    private static final String SELECT_SQL_LIMIT_ORDER = "SELECT * FROM %s WHERE %s ORDER BY %s %s LIMIT %d, %d";

    public static final int AESC = 1;

    public static final int DESC = -1;

    protected final static Logger logger = LoggerFactory.getLogger(QueryHelper.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public QueryHelper() {}

    public boolean insert(Class<?> entityClass, Object[] cols, Object ...params) {
        String tableName = entityClass.getAnnotation(Table.class).name();
        String columns = " (";
        String values = " VALUES(";
        for(int i=0; i<cols.length; i++) {
            columns += cols[i];
            values += "?";
            if(i < cols.length-1) {
                columns += ",";
                values += ",";
            }
        }
        columns += ")";
        values += ")";

        String SQL = "INSERT INTO " + tableName + columns + values;
        logger.info(String.format("Prepared to execute SQL = %s and params : [%s]", SQL, params));

        return jdbcTemplate.update(SQL, params) > 0 ? true : false;
    }

    public boolean batchInsert(Class<?> entityClass, Object[] cols, List<Object[]> params) {
        String tableName = entityClass.getAnnotation(Table.class).name();
        String columns = " (";
        String values = " VALUES(";
        for(int i=0; i<cols.length; i++) {
            columns += cols[i];
            values += "?";
            if(i < cols.length-1) {
                columns += ",";
                values += ",";
            }
        }
        columns += ")";
        values += ")";

        String SQL = "INSERT INTO " + tableName + columns + values;
        logger.info(String.format("Prepared to execute SQL = %s and params : [%s]", SQL, params));

        return jdbcTemplate.batchUpdate(SQL, params).length > 0 ? true : false;
    }

    public long count(Class<?> entityClass, String conditions, Object ...params) {
        String tableName = entityClass.getAnnotation(Table.class).name();

        String SQL = "SELECT COUNT(*) FROM " + tableName + " WHERE " + conditions;
        logger.info(String.format("Prepared to execute SQL = %s and params : [%s]", SQL, params));

        return jdbcTemplate.queryForObject(SQL, Integer.class, params);
    }

    public boolean isExist(Class<?> entityClass, String conditions, Object ...params) {

       return count(entityClass, conditions, params) > 0 ? true : false;
    }

    public boolean update(Class<?> entityClass, String cols, String conditions, Object ...params) {

        String tableName = entityClass.getAnnotation(Table.class).name();
        String SQL = "UPDATE " + tableName + " SET " + cols + " WHERE " + conditions;

        logger.info(String.format("Prepared to execute SQL = %s and params : [%s]", SQL, params));

        return jdbcTemplate.update(SQL, params) > 0 ? true : false;

    }

    public void batchUpdate(Class<?> entityClass, String cols, String conditions, List<Object[]> values) {

        String tableName = entityClass.getAnnotation(Table.class).name();
        String SQL = "UPDATE " + tableName + " SET " + cols + " WHERE " + conditions;

        PreparedStatement psmt = null;
        try {
            psmt = jdbcTemplate.getDataSource().getConnection().prepareStatement(SQL);

            for(int i=0; i<values.size(); i++) {
                for(int j=0; j<values.get(i).length; j++) {
                    psmt.setObject(j+1, values.get(i)[j]);
                }
                psmt.addBatch();
            }
            psmt.executeBatch();
            psmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                psmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public boolean delete(Class<?> entityClass, String conditions, Object ...params) {

        String tableName = entityClass.getAnnotation(Table.class).name();
        String SQL = "DELETE FROM " + tableName + " WHERE " + conditions;
        logger.info(String.format("Prepared to execute SQL = %s and params : [%s]", SQL, params));

        return jdbcTemplate.update(SQL, params) > 0 ? true : false;
    }

    public boolean batchDelete(Class<?> entityClass, String conditions, List<Object[]> values) {
        String tableName = entityClass.getAnnotation(Table.class).name();
        String SQL = "DELETE FROM " + tableName + " WHERE " + conditions;
        logger.info(String.format("Prepared to execute SQL = %s and params : [%s]", SQL, values));

        PreparedStatement psmt = null;
        try {
            psmt = jdbcTemplate.getDataSource().getConnection().prepareStatement(SQL);

            for(int i=0; i<values.size(); i++) {
                for(int j=0; j<values.get(i).length; j++) {
                    psmt.setObject(j+1, values.get(i)[j]);
                }
                psmt.addBatch();
            }
            psmt.executeBatch();
            psmt.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                psmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public <T> List<T> findAll(Class<T> entityClass, int startPos, int endPos, String sortProperty, int sequence,
                               String conditions, Object ...params) {

        String tableName = entityClass.getAnnotation(Table.class).name();

        String SQL = null;

        if(startPos < 0 && sortProperty == null) {
            SQL = String.format(SIMPLE_SELECT_SQL, tableName, conditions);
        }
        if(startPos < 0 && sortProperty != null) {
            SQL = String.format(SELECT_SQL_ORDER, tableName, conditions, sortProperty, sequence > 0 ? "ASC" : "DESC");
        }
        if(startPos >= 0 && sortProperty == null) {
            SQL = String.format(SELECT_SQL_LIMIT, tableName, conditions, startPos, endPos);
        }
        if(startPos >= 0 && sortProperty != null) {
//            SQL = String.format(SELECT_SQL_LIMIT_ORDER, tableName, conditions, startPos, endPos,
//                    sortProperty, sequence > 0 ? "AESC" : "DESC");
            SQL = String.format(SELECT_SQL_LIMIT_ORDER, tableName, conditions, sortProperty, sequence > 0 ? "ASC" : "DESC", startPos, endPos);
        }

        logger.info(String.format("Prepared to execute SQL = %s and params : [%s]", SQL, params));

        Field[] fields = entityClass.getDeclaredFields();
        List<T> resuList = jdbcTemplate.query(SQL, new RowMapper<T>(){
            @Override
            public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                T entity = null;
                try {
                    entity = entityClass.newInstance();
                    for(Field field : fields) {
                        Field privateField = entity.getClass().getDeclaredField(field.getName());
                        privateField.setAccessible(true);
                        if(privateField.getDeclaredAnnotation(Transient.class) != null)
                            continue;

                        if(privateField.getDeclaredAnnotation(Column.class) != null && !privateField.getDeclaredAnnotation(Column.class).name().isEmpty())
                            privateField.set(entity, rs.getObject(field.getAnnotation(Column.class).name(), field.getType()));
                        else privateField.set(entity, rs.getObject(field.getName(), field.getType()));

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

                return entity;
            }

        }, params);

        return resuList;
    }

    public <T> List<T> findAll(Class<T> entityClass, String conditions, Object ...params) {
        return findAll(entityClass, -1, -1, null, -1, conditions, params);
    }

//    public <T> List<T> findAll(Class<T> entityClass, String conditions, Object ...params) {
//        return findAll(entityClass, -1, -1, null, -1, conditions, params);
//    }

//    public <T> List<T> findByPage(Class<T> entityClass, PageModel pageModel, String conditions, Object ...params) {
//        return findByPage(entityClass, pageModel, null, -1, conditions, params);
//    }

    public <T> List<T> findByPage(Class<T> entityClass, PageModel pageModel, String sortProperty, int sequence,
                                  String conditions, Object ...params) {
        return findAll(entityClass, pageModel.getStartIndex(), pageModel.getPageSize(), sortProperty, sequence, conditions, params);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}

