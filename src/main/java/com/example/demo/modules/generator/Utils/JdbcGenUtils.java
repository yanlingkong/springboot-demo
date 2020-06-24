package com.example.demo.modules.generator.Utils;

import com.example.demo.common.utils.CommonUtils;
import com.example.demo.common.utils.DateUtils;
import com.example.demo.common.utils.JdbcUtils;
import com.example.demo.common.utils.PropertiesUtils;
import com.example.demo.modules.generator.entity.ColumnEntity;
import com.example.demo.modules.generator.entity.TableEntity;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.*;

/**
 * @Auther: liuc
 * @Date: 18-12-16 16:04
 * @email i@liuchaoboy.com
 * @Description: 代码生成工具类（使用jdbc生成本地代码）
 */
public class JdbcGenUtils {

    //生成代码 main 方法
    public static void main(String[] args) throws Exception {
        //连接要生成代码的数据库 输入账号密码
        String jdbcDriver = "com.mysql.jdbc.Driver";
        String jdbcUrl = "jdbc:mysql://47.92.112.206:13308/testmaster?useUnicode=true&characterEncoding=utf-8";
        String jdbcUsername = "testmaster";
        String jdbcPassword = "hbZJKbank2019$";
        //数据库表，根据 tableSql 查询所要生成的表
        String tablePrefix = "test10_fee_";
        //java 代码生成目录
        String javaModule = "rooms";

        generatorCode(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword, tablePrefix, javaModule);

    }



    public static void generatorCode(String jdbcDriver, String jdbcUrl, String jdbcUsername, String jdbcPassword,
                                     String tablePrefix, String javaModule) throws Exception {

        String rootPath = "";
        String osName = "os.name";
        String osWindows = "win";
        if(!System.getProperty(osName).toLowerCase().startsWith(osWindows)) {
            rootPath = "/";
        }
        String tableSql = "SELECT table_name, table_comment FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = (SELECT DATABASE()) AND table_name LIKE '%" + tablePrefix + "%';";
        JdbcUtils jdbcUtils = new JdbcUtils(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword);
        List<Map> tableList = jdbcUtils.selectByParams(tableSql, null);
        TableEntity table = null;
        List<TableEntity> tables = new ArrayList<>();
        Iterator<Map> tableIterator = tableList.iterator();
        while(tableIterator.hasNext()) {
            Map<String, String> currTable = tableIterator.next();
            table = new TableEntity();
            String tableName = currTable.get("TABLE_NAME");
            String className = GenUtils.tableToJava(tableName, "");
            table.setTableName(tableName);
            table.setClassName(className);
            table.setObjName(StringUtils.uncapitalize(className));
            table.setTableComment(currTable.get("TABLE_COMMENT"));

            String columnSql = "SELECT column_name,data_type,column_comment,column_key,extra FROM information_schema.columns WHERE TABLE_NAME = '"+ tableName + "' AND table_schema = (SELECT DATABASE()) ORDER BY ordinal_position";
            ColumnEntity columnEntity = null;
            List<Map> columnList = jdbcUtils.selectByParams(columnSql,null);
            Iterator<Map> columnIterator = columnList.iterator();
            while(columnIterator.hasNext()){
                Map<String, String> currColumn = columnIterator.next();
                columnEntity = new ColumnEntity();
                columnEntity.setExtra(currColumn.get("EXTRA"));

                String columnName = currColumn.get("COLUMN_NAME");
                String methodName = GenUtils.columnToJava(columnName);
                columnEntity.setColumnName(columnName);
                columnEntity.setFieldName(StringUtils.uncapitalize(methodName));
                columnEntity.setMethodName(methodName);

                columnEntity.setColumnKey(currColumn.get("COLUMN_KEY"));
                columnEntity.setDataType(currColumn.get("DATA_TYPE"));
                columnEntity.setColumnComment(currColumn.get("COLUMN_COMMENT"));

                // 属性类型
                columnEntity.setFieldType(PropertiesUtils.getInstance("template/config").get(columnEntity.getDataType()));

                // 主键判断
                if ("PRI".equals(columnEntity.getColumnKey()) && table.getPk() == null) {
                    table.setPk(columnEntity);
                }

                table.addColumn(columnEntity);
            }
            tables.add(table);
        }

        // 没主键，则第一个字段为主键
        if (table.getPk() == null) {
            table.setPk(table.getColumns().get(0));
        }

        String projectPath = getProjectPath();
        System.out.println("===>>>java generation path:" + projectPath +"/src/main/java");
        Map<String, Object> map = null;
        for (TableEntity tableEntity : tables) {
            // 封装模板数据
            map = new HashMap<String, Object>(16);
            map.put("tableName", tableEntity.getTableName());
            map.put("comments", tableEntity.getTableComment());
            map.put("pk", tableEntity.getPk());
            map.put("className", tableEntity.getClassName());
            map.put("objName", tableEntity.getObjName());
            map.put("requestMapping", tableEntity.getTableName().replace("_","/"));
            map.put("authKey", GenUtils.urlToAuthKey(tableEntity.getTableName().replace("_","/")));
            map.put("columns", tableEntity.getColumns());
            map.put("hasDecimal", tableEntity.buildHasDecimal().getHasDecimal());
            map.put("package", PropertiesUtils.getInstance("template/config").get("package"));
            map.put("module", javaModule);
            map.put("author", PropertiesUtils.getInstance("template/config").get("author"));
            map.put("email", PropertiesUtils.getInstance("template/config").get("email"));
            map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_PATTERN_SLASH));
            System.out.println("============ start table: " + tableEntity.getTableName() + " ================");

            for (String template : GenUtils.getTemplates()) {
                String filePath = getFileName(template, javaModule,tableEntity.getClassName(), rootPath);
                String templatePath = rootPath + JdbcUtils.class.getResource("/" + template).getPath().replaceFirst("/", "");
                System.out.println(filePath);
                File dstDir = new File(CommonUtils.getPath(filePath));
                //文件夹不存在创建文件夹
                if(!dstDir.exists()){
                    dstDir.mkdirs();
                }
                File dstFile = new File(filePath);
                //文件不存在则创建
                if(!dstFile.exists()){
                    CommonUtils.generate(templatePath, filePath, map);
                    System.out.println(filePath + "===>>>创建成功！");
                } else {
                    System.out.println(filePath + "===>>>文件已存在，未重新生成！");
                }
            }
            System.out.println("============ finish table: " + tableEntity.getTableName() + " ================\n");
        }
    }


    public static String getFileName(String template, String javaModule, String className, String rootPath) {
        String packagePath = rootPath + getProjectPath() + "/src/main/java/" + PropertiesUtils.getInstance("template/config").get("package").replace(".","/") + "/modules/" + javaModule + "/";
        String resourcePath = rootPath + getProjectPath() + "/src/main/resources/";
        if (template.contains(GenConstant.JAVA_ENTITY)) {
            return packagePath + "entity/" + className + "Entity.java";
        }

        if (template.contains(GenConstant.JAVA_MAPPER)) {
            return packagePath + "dao/" + className + "Mapper.java";
        }

        if (template.contains(GenConstant.XML_MAPPER)) {
            return packagePath + "mapper/" + className + "Mapper.xml";
        }

        if (template.contains(GenConstant.JAVA_SERVICE)) {
            return packagePath + "service/" + className + "Service.java";
        }

        if (template.contains(GenConstant.JAVA_SERVICE_IMPL)) {
            return packagePath + "service/impl/" + className + "ServiceImpl.java";
        }

        if (template.contains(GenConstant.JAVA_CONTROLLER)) {
            return packagePath + "controller/" + className + "Controller.java";
        }

        return null;
    }

    public static String getProjectPath() {
        String basePath = JdbcUtils.class.getResource("/").getPath().replace("/target/classes/", "").replaceFirst("/", "");
        return basePath;
    }

}
