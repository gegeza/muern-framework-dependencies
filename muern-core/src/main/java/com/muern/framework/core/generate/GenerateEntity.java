package com.muern.framework.core.generate;

import com.muern.framework.core.utils.DateUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gegeza
 * @date 2019-12-17 17:30 AM
 */
public final class GenerateEntity {

    private static final Pattern PATTERN= Pattern.compile("([A-Za-z\\d]+)(_)?");

    /** 生成实体类 */
    public static void generate(String tableName, String entityName, String entityPackage, String entityPath,
                                List<String> colnames, List<String> colTypes, List<String> colComment, boolean importDate,
                                boolean importDateTime, boolean importBigDecimal) throws IOException {
        StringBuffer sb;
        //package code
        sb = new StringBuffer().append("package ").append(entityPackage).append(";\r\n\r\n");
        sb.append("import com.muern.framework.boot.common.BaseDTO;\r\n");
        sb.append("import io.mybatis.provider.Entity;\r\n");
        if (importBigDecimal) {
            sb.append("import java.math.BigDecimal;\r\n");
        }
        if (importDate) {
            sb.append("import java.time.LocalDate;\r\n");
        }
        if (importDateTime) {
            sb.append("import java.time.LocalDateTime;\r\n");
        }
        sb.append("\r\n");
        //comment code
        sb.append("/**\r\n");
        sb.append(" * @author mybatis-mapper generate\r\n");
        sb.append(" * @date ".concat(DateUtils.formatNowDate()).concat("\r\n"));
        sb.append(" */\r\n");
        sb.append("@Entity.Table(\"".concat(tableName).concat("\")\r\n"));
        //class code
        sb.append("public class ".concat(entityName).concat(" extends BaseDTO {\r\n\r\n"));
        sb.append("\tprivate static final long serialVersionUID = ").append(generateSerialVersionUid()).append("L;\r\n\r\n");
        for (int i = 0; i < colnames.size(); i++) {
            sb.append("\t/** ").append(colComment.get(i)).append(" */\r\n");
            if (i == 0) {
                sb.append("\t@Entity.Column(").append("id = true)\r\n");
            }
            sb.append("\tprivate ").append(getType(colTypes.get(i))).append(" ").append(underline2Camel(colnames.get(i))).append(";\r\n\r\n");
        }
        //生成Getter/Setter
        for (int i = 0; i < colnames.size(); i++) {
            String field = underline2Camel(colnames.get(i));
            sb.append("\tpublic ").append(getType(colTypes.get(i))).append(" ").append("get")
                    .append(field.substring(0, 1).toUpperCase()).append(field.substring(1)).append("() {\r\n")
                    .append("\t\treturn ").append(field).append(";\r\n\t").append("}\r\n\r\n");
            sb.append("\tpublic void set").append(field.substring(0, 1).toUpperCase()).append(field.substring(1))
                    .append("(").append(getType(colTypes.get(i))).append(" ").append(field).append(") {\r\n\t\t")
                    .append("this.").append(field).append(" = ").append(field).append(";").append("\r\n\t").append("}\r\n\r\n");
        }
        sb.append("}");
        //生成文件
        String path = entityPath.concat("/src/main/java/").concat(entityPackage.replaceAll("\\.", "/")).concat("/");
        File file = new File(path);
        if (!file.exists() && !file.mkdir()) {
            System.out.println("mkdir error");
            return;
        }
        file = new File(file, entityName + ".java");
        if (!file.exists() && !file.createNewFile()) {
            System.out.println("createNewFile error");
            return;
        }
        try (
            PrintWriter pw = new PrintWriter(new FileWriter(file))
        ) {
            pw.print(sb.toString());
            pw.flush();
            System.out.println("Generate Successful :" + file.getPath());
        }
    }

    /** 根据SQL类型获取java类型 */
    private static String getType(String sqlType) {
        switch (sqlType) {
            case "bit":
                return "Boolean";
            case "tinyint":
            case "smallint":
            case "int":
            case "integer":
                return "Integer";
            case "bigint":
                return "Long";
            case "float":
            case "double":
            case "numeric":
                return "Double";
            case "decimal":
                return "BigDecimal";
            case "varchar":
            case "char":
            case "text":
            case "mediumtext":
                return "String";
            case "date":
                return "LocalDate";
            case "time":
            case "datetime":
            case "timestamp":
                return "LocalDateTime";
            default:
                System.out.println("ERROR DATA TYPE : "+ sqlType);
                return null;
        }
    }

    /** 下划线转首字母大写 */
    private static String underline2Camel(String line){
        if(StringUtils.isEmpty(line)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Matcher matcher = PATTERN.matcher(line);
        while(matcher.find()){
            String word = matcher.group();
            sb.append(matcher.start()==0? Character.toLowerCase(word.charAt(0)): Character.toUpperCase(word.charAt(0)));
            int index = word.lastIndexOf('_');
            if(index > 0){
                sb.append(word.substring(1, index).toLowerCase());
            }else{
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    private static long generateSerialVersionUid() {
        return (long) (Math.random() * 1000000000000000000L);
    }
}
