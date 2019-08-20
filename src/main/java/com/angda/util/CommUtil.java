package com.angda.util;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 封装所有的公共操作，包括加载配置文件，json操作
 */
public class CommUtil {
    public static  Properties loadProperties(String fileName){
        Properties properties=new Properties();
        InputStream in=CommUtil.class.getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(in);
        } catch (IOException e) {
            System.out.println("资源文件加载失败");
            e.printStackTrace();
            return null;
        }
        return properties;
    }

}
