package com.muern.framework.core.generate;

import com.muern.framework.core.utils.DateUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author gegeza
 * @date 2019-12-17 17:48 AM
 */
public class GenerateServiceImpl {

    /** 生成ServiceImpl类 */
    public static void generate(String entityName, String entityPackage, String dtoPackage, String mapperPackage, String servicePackage, String servicePath) throws IOException {
        String obj = entityName.substring(0, 1).toLowerCase().concat(entityName.substring(1));
        StringBuilder sb;
        //package code
        sb = new StringBuilder().append("package ".concat(servicePackage).concat(".impl;\r\n\r\n"));
        //import code

        sb.append("import ").append(entityPackage).append(".").append(entityName).append(";\r\n");
        sb.append("import ").append(dtoPackage).append(".").append(entityName).append("DTO;\r\n");
        sb.append("import ".concat(mapperPackage).concat(".").concat(entityName).concat("Mapper;\r\n"));
        sb.append("import ".concat(servicePackage).concat(".").concat(entityName).concat("Service;\r\n"));
        sb.append("import com.github.pagehelper.PageHelper;\r\n");
        sb.append("import com.github.pagehelper.PageInfo;\r\n");
        sb.append("import io.mybatis.mapper.example.ExampleWrapper;\r\n");
        sb.append("import org.springframework.beans.BeanUtils;\r\n");
        sb.append("import org.springframework.stereotype.Service;\r\n\r\n");
        sb.append("import javax.annotation.Resource;\r\n");
        sb.append("import java.util.List;\r\n\r\n");
        //comment code
        sb.append("/**\r\n");
        sb.append(" * @author mybatis-mapper generate\r\n");
        sb.append(" * @date ".concat(DateUtils.formatNowDate()).concat("\r\n"));
        sb.append(" */\r\n");
        //annotation code
        sb.append("@Service\r\n");
        //class code
        sb.append("public class ".concat(entityName).concat("ServiceImpl implements ").concat(entityName).concat("Service {\r\n\r\n"));
        sb.append("\t@Resource private ".concat(entityName).concat("Mapper mapper;\r\n\r\n"));
        //page method
        sb.append("\t@Override\r\n\tpublic PageInfo<").append(entityName).append("> page(").append(entityName).append("DTO dto) {\r\n")
                .append("\t\t//构建查询条件\r\n")
                .append("\t\tExampleWrapper<").append(entityName).append(", Long> wrapper = mapper.wrapper();\r\n")
                .append("\t\t//TODO 添加查询条件 使用方法见：https://mapper.mybatis.io/docs/1.getting-started.html#_1-4-4-wrapper-%E7%94%A8%E6%B3%95\r\n")
                .append("\t\t//调用分页查询方法\r\n")
                .append("\t\tPageHelper.startPage(dto.getPageNum(), dto.getPageSize());\r\n")
                .append("\t\treturn new PageInfo<>(wrapper.list());\r\n\t}\r\n\r\n");
        //list method
        sb.append("\t@Override\r\n\tpublic List<").append(entityName).append("> list(").append(entityName).append("DTO dto) {\r\n")
                .append("\t\t//构建查询条件\r\n")
                .append("\t\tExampleWrapper<").append(entityName).append(", Long> wrapper = mapper.wrapper();\r\n")
                .append("\t\t//TODO 添加查询条件 使用方法见：https://mapper.mybatis.io/docs/1.getting-started.html#_1-4-4-wrapper-%E7%94%A8%E6%B3%95\r\n")
                .append("\t\t//调用分页查询方法\r\n")
                .append("\t\treturn wrapper.list();\r\n\t}\r\n\r\n");
        //detail method
        sb.append("\t@Override\r\n\tpublic ").append(entityName).append(" detail(").append(entityName).append("DTO dto, Long id) {\r\n")
                .append("\t\tif (id != null) {\r\n\t\t\treturn mapper.selectByPrimaryKey(id).orElse(null);\r\n")
                .append("\t\t} else {\r\n\t\t\t").append(entityName).append(" ").append(obj).append(" = new ").append(entityName).append("();\r\n")
                .append("\t\t\tBeanUtils.copyProperties(dto, ").append(obj).append(");\r\n")
                .append("\t\t\treturn mapper.selectOne(").append(obj).append(").orElse(null);\r\n\t\t}\r\n\t}\r\n\r\n");
        //create method
        sb.append("\t@Override\r\n\tpublic void create(").append(entityName).append("DTO dto) {\r\n")
                .append("\t\t//构建对象\r\n")
                .append("\t\t").append(entityName).append(" ").append(obj).append(" = new ").append(entityName).append("();\r\n")
                .append("\t\tBeanUtils.copyProperties(dto, ").append(obj).append(");\r\n")
                .append("\t\t//TODO setting create field\r\n\t\t//调用新增方法\r\n")
                .append("\t\tmapper.insertSelective(").append(obj).append(");\r\n\t}\r\n\r\n");
        //modify method
        sb.append("\t@Override\r\n\tpublic void modify(").append(entityName).append("DTO dto) {\r\n")
                .append("\t\t//构建对象\r\n")
                .append("\t\t").append(entityName).append(" ").append(obj).append(" = new ").append(entityName).append("();\r\n")
                .append("\t\tBeanUtils.copyProperties(dto, ").append(obj).append(");\r\n")
                .append("\t\t//TODO setting modify field\r\n\t\t//调用更新方法\r\n")
                .append("\t\tmapper.updateByPrimaryKeySelective(").append(obj).append(");\r\n\t}\r\n");
        sb.append("}");
        //write to file
        String path = servicePath.concat("/src/main/java/").concat(servicePackage.replaceAll("\\.", "/")).concat("/impl/");
        File file = new File(path);
        if (!file.exists() && !file.mkdir()) {
            System.out.println("mkdir error");
            return;
        }
        file = new File(file, entityName + "ServiceImpl.java");
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
