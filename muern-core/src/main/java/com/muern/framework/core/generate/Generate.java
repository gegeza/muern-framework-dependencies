package com.muern.framework.core.generate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gegeza
 * @date 2019-12-17 17:50 AM
 */
public class Generate {
    private static List<String> colnames;
    private static List<String> colTypes;
    private static List<String> colComment;
    private static Boolean importDate = false;
    private static Boolean importDateTime = false;
    private static Boolean importBigDecimal = false;

    private static void initTable(String host, String port, String database, String username,
                                  String password, String tableName) throws Exception {
        colnames = new ArrayList<>();
        colTypes = new ArrayList<>();
        colComment = new ArrayList<>();
        importDate = false;
        importDateTime = false;
        importBigDecimal = false;
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
        //读一行记录;
        String strsql = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_KEY, COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS "
                .concat("WHERE TABLE_SCHEMA = '").concat(database).concat("' AND TABLE_NAME = '" + tableName + "'");
        try (
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement pstmt = conn.prepareStatement(strsql)
        ) {
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                colnames.add(result.getString(1));
                colTypes.add(result.getString(2));
                colComment.add(result.getString(4));
                if ("date".equals(result.getString(2))) {
                    importDate = true;
                }
                if ("datetime".equals(result.getString(2)) || "timestamp".equals(result.getString(2))) {
                    importDateTime = true;
                }
                if ("decimal".equals(result.getString(2))) {
                    importBigDecimal = true;
                }
            }
        }

    }

    /**
     * @param host        数据库IP
     * @param port        数据库端口号
     * @param database    数据库名
     * @param username    数据库用户名
     * @param password    数据库密码
     * @param tableName   表名
     * @param entityName  实体类名
     * @throws Exception 抛出异常
     */
    public static void generate(String host, String port, String database, String username, String password,
                                String tableName, String entityName, String entityPackage, String entityPath, 
                                String dtoPackage, String dtoPath, String mapperPackage, String mapperPath, String servicePackage, String servicePath,
                                String controllerPackage, String controllerPath) {
        try {
            //读取表结构
            initTable(host, port, database, username, password, tableName);
            //生成实体类
            GenerateEntity.generate(tableName, entityName, entityPackage, entityPath, colnames, colTypes, colComment, importDate, importDateTime, importBigDecimal);
            GenerateDTO.generate(entityName, entityPackage, dtoPackage, dtoPath);
            //生成Mapper
            GenerateMapper.generate(entityName, entityPackage, mapperPackage, mapperPath);
            //生成Service
            GenerateService.generate(entityName, entityPackage, dtoPackage, servicePackage, servicePath);
            //生成ServiceImpl
            GenerateServiceImpl.generate(entityName, entityPackage, dtoPackage, mapperPackage, servicePackage, servicePath);
            //生成Controller
            GenerateController.generate(entityName, entityPackage, dtoPackage, servicePackage, controllerPackage, controllerPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
