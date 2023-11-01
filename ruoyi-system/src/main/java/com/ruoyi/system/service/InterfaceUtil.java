package com.ruoyi.system.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface InterfaceUtil {
    List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException;

    boolean executeSql(String sql, Object... params) throws SQLException;

    int[] executeBatch(String... sql) throws SQLException;

    <T> T getBean(String name, Class<T> clazz);

    Object getBean(String name);

    void DS(String dataSource);

    void DS_done();
}
