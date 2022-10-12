package com.muern.framework.core.generate;

import com.muern.framework.core.utils.DateUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author gegeza
 * @date 2019-12-17
 */
public final class GenerateMapper {
    /** 生成Mapper类 */
    public static void generate(String entityName, String entityPackage, String mapperPackage, String mapperPath) throws IOException {
        StringBuffer sb;
        //package code
        sb = new StringBuffer().append("package ".concat(mapperPackage).concat(";\r\n\r\n"));
        //import code
        sb.append("import ").append(entityPackage).append(".").append(entityName).append(";\r\n\r\n");
        sb.append("import org.apache.ibatis.annotations.Mapper;\r\n\r\n");
        //comment code
        sb.append("/**\r\n");
        sb.append(" * @author mybatis-mapper generate\r\n");
        sb.append(" * @date ".concat(DateUtils.formatNowDate()).concat("\r\n"));
        sb.append(" */\r\n");
        //annotation code
        sb.append("@Mapper\r\n");
        //interface code
        sb.append("public interface ".concat(entityName).concat("Mapper extends io.mybatis.mapper.Mapper<").concat(entityName).concat(", Long> {\r\n"));
        sb.append("}");
        //write file
        String path = mapperPath.concat("/src/main/java/").concat(mapperPackage.replaceAll("\\.", "/")).concat("/");
        File file = new File(path);
        if (!file.exists() && !file.mkdir()) {
            System.out.println("mkdir error");
            return;
        }
        file = new File(file, entityName + "Mapper.java");
        if (!file.exists() && !file.createNewFile()) {
            System.out.println("createNewFile error");
            return;
        }
        try (
            PrintWriter pw = new PrintWriter(new FileWriter(file))
        ) {
            pw.println(sb.toString());
            pw.flush();
            pw.close();
            System.out.println("Generate Successful :" + file.getPath());
        }
    }
}
