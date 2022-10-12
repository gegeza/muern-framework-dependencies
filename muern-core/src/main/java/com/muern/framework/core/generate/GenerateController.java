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
public class GenerateController {

    /** 生成ServiceImpl类 */
    public static void generate(String entityName, String entityPackage, String dtoPackage, String servicePackage, String controllerPackage, String controllerPath) throws IOException {
        StringBuilder sb;
        //package code
        sb = new StringBuilder().append("package ".concat(controllerPackage).concat(";\r\n\r\n"));
        //import code
        sb.append("import ".concat(servicePackage).concat(".").concat(entityName).concat("Service;\r\n"));
        sb.append("import org.slf4j.Logger;\r\n");
        sb.append("import org.slf4j.LoggerFactory;\r\n");
        sb.append("import org.springframework.web.bind.annotation.RequestMapping;\r\n");
        sb.append("import org.springframework.web.bind.annotation.RestController;\r\n");
        sb.append("import javax.annotation.Resource;\r\n\r\n");
        //comment code
        sb.append("/**\r\n");
        sb.append(" * @author mybatis-mapper generate\r\n");
        sb.append(" * @date ".concat(DateUtils.formatNowDate()).concat("\r\n"));
        sb.append(" */\r\n");
        //annotation code
        sb.append("@RestController\r\n");
        sb.append("@RequestMapping(\"\")\r\n");
        //class code
        sb.append("public class ".concat(entityName).concat("Controller {\r\n\r\n"));
        sb.append("\tprivate static final Logger LOGGER = LoggerFactory.getLogger(").append(entityName).append("Controller.class);\r\n\r\n");
        sb.append("\t@Resource private ".concat(entityName).concat("Service service;\r\n\r\n"));
        sb.append("}");
        //write to file
        String path = controllerPath.concat("/src/main/java/").concat(controllerPackage.replaceAll("\\.", "/")).concat("/");
        File file = new File(path);
        if (!file.exists() && !file.mkdir()) {
            System.out.println("mkdir error");
            return;
        }
        file = new File(file, entityName + "Controller.java");
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
