package com.muern.framework.core.generate;

import com.muern.framework.core.utils.DateUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author gegeza
 * @date 2019-12-17 17:45 AM
 */
public class GenerateService {

    /** 生成Service类 */
    public static void generate(String entityName, String entityPackage, String dtoPackage, String servicePackage, String servicePath) throws IOException {
        StringBuffer sb;
        //package code
        sb = new StringBuffer().append("package ".concat(servicePackage).concat(";\r\n\r\n"));
        //import code
        sb.append("import ").append(dtoPackage).append(".").append(entityName).append("DTO;\r\n");
        sb.append("import ").append(entityPackage).append(".").append(entityName).append(";\r\n");
        sb.append("import com.github.pagehelper.PageInfo;\r\n\r\n");
        sb.append("import java.util.List;\r\n\r\n");
        //comment code
        sb.append("/**\r\n");
        sb.append(" * @author mybatis-mapper generate\r\n");
        sb.append(" * @date ".concat(DateUtils.formatNowDate()).concat("\r\n"));
        sb.append(" */\r\n");
        //interface code
        sb.append("public interface ").append(entityName).append("Service {\r\n\r\n");
        sb.append("\t/**\r\n\t * 查询分页数据\r\n\t * @param dto 分页条件\r\n\t * @return com.github.pagehelper.PageInfo<")
                .append(entityPackage).append(entityName).append(">\r\n\t */\r\n");
        sb.append("\tPageInfo<").append(entityName).append("> page(").append(entityName).append("DTO dto);\r\n\r\n");
        sb.append("\t/**\r\n\t * 查询列表数据\r\n\t * @param dto 列表条件\r\n\t * @return java.util.List<")
                .append(entityPackage).append(entityName).append(">\r\n\t */\r\n");
        sb.append("\tList<").append(entityName).append("> list(").append(entityName).append("DTO dto);\r\n\r\n");
        sb.append("\t/**\r\n\t * 查询详细数据\r\n\t * @param dto 查询条件\r\n\t * @param id 主键\r\n\t * @return ")
                .append(entityPackage).append(entityName).append("\r\n\t */\r\n");
        sb.append("\t").append(entityName).append(" detail(").append(entityName).append("DTO dto, Long id);\r\n\r\n");
        sb.append("\t/**\r\n\t * 插入或修改数据 根据主键判断\r\n\t * @param dto 插入或修改数据\r\n\t */\r\n");
        sb.append("\tvoid com(").append(entityName).append("DTO dto);\r\n");
        sb.append("}");
        //写入文件
        String path = servicePath.concat("/src/main/java/").concat(servicePackage.replaceAll("\\.", "/")).concat("/");
        File file = new File(path);
        if (!file.exists() && !file.mkdir()) {
            System.out.println("mkdir error");
            return;
        }
        file = new File(file, entityName + "Service.java");
        if (!file.exists() && !file.createNewFile()) {
            System.out.println("createNewFile error");
            return;
        }
        try (
            PrintWriter pw = new PrintWriter(new FileWriter(file))
        ) {
            pw.print(sb.toString());
            pw.flush();
            pw.close();
            System.out.println("Generate Successful :" + file.getPath());
        }
    }
}
