package com.muern.framework.core.generate;

import com.muern.framework.core.utils.DateUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author gegeza
 * @date 2019-12-17 17:30 AM
 */
public final class GenerateDTO {

    /** 生成实体类 */
    public static void generate(String entityName, String entityPackage, String dtoPackage, String dtoPath) throws IOException {
        StringBuffer sb;
        //package code
        sb = new StringBuffer().append("package ").append(dtoPackage).append(";\r\n\r\n");
        //import code
        sb.append("import ").append(entityPackage).append(".").append(entityName).append(";\r\n\r\n");
        //comment code
        sb.append("/**\r\n");
        sb.append(" * @author mybatis-mapper generate\r\n");
        sb.append(" * @date ".concat(DateUtils.formatNowDate()).concat("\r\n"));
        sb.append(" */\r\n");
        //class code
        sb.append("public class ".concat(entityName).concat("DTO extends ").concat(entityName).concat(" {\r\n\r\n"));
        sb.append("}");
        //生成文件
        String path = dtoPath.concat("/src/main/java/").concat(dtoPackage.replaceAll("\\.", "/")).concat("/");
        File file = new File(path);
        if (!file.exists() && !file.mkdir()) {
            System.out.println("mkdir error");
            return;
        }
        file = new File(file, entityName + "DTO.java");
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
