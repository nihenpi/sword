package com.skaz.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author jungle
 */
public abstract class Props {

    private static final Logger logger = LoggerFactory.getLogger(Props.class);

    // 加载属性文件
    public static Properties load(String path) {
        Properties props = new Properties();
        InputStream is = null;
        try {
            if (Strings.isEmpty(path)) {
                throw new IllegalArgumentException();
            }
            String suffix = ".properties";
            if (path.lastIndexOf(suffix) == -1) {
                path += suffix;
            }
            is = Classes.getClassLoader().getResourceAsStream(path);
            if (is != null) {
                props.load(is);
            } else {
                throw new FileNotFoundException("未找到文件：" + path);
            }
        } catch (Exception e) {
            logger.error("加载属性文件出错！path：" + path, e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                logger.error("释放资源出错！", e);
            }
        }
        return props;
    }

    /**
     * 转换为Map
     */
    public static Map<String, String> toMap(Properties props) {
        Map<String, String> map = new HashMap<String, String>();
        for (String key : props.stringPropertyNames()) {
            map.put(key, props.getProperty(key));
        }
        return map;
    }

    /**
     * 加载属性文件，并转为 Map
     */
    public static Map<String, String> loadToMap(String path) {
        Properties props = load(path);
        return toMap(props);
    }

    /**
     * 获取字符型属性
     */
    public static String getString(Properties props, String key) {
        String value = "";
        if (props.containsKey(key)) {
            value = props.getProperty(key);
        }
        return value;
    }

    /**
     * 获取字符型属性（带有默认值）
     */
    public static String getString(Properties props, String key, String defalutValue) {
        String value = defalutValue;
        if (props.containsKey(key)) {
            value = props.getProperty(key);
        }
        return value;
    }

    public static List<String> getStringList(Properties props, String key) {
        return getStringList(props, key, ",");
    }

    public static List<String> getStringList(Properties props, String key, String prefix) {
        return Strings.toList(getString(props, key), prefix);
    }

    /**
     * 获取数值型属性
     */
    public static int getNumber(Properties props, String key) {
        int value = 0;
        if (props.containsKey(key)) {
            value = Casts.toInt(props.getProperty(key));
        }
        return value;
    }

    // 获取数值型属性（带有默认值）
    public static int getNumber(Properties props, String key, int defaultValue) {
        int value = defaultValue;
        if (props.containsKey(key)) {
            value = Casts.toInt(props.getProperty(key));
        }
        return value;
    }

    /**
     * 获取布尔型属性
     */
    public static boolean getBoolean(Properties props, String key) {
        return getBoolean(props, key, false);
    }

    /**
     * 获取布尔型属性（带有默认值）
     */
    public static boolean getBoolean(Properties props, String key, boolean defalutValue) {
        boolean value = defalutValue;
        if (props.containsKey(key)) {
            value = Casts.toBoolean(props.getProperty(key));
        }
        return value;
    }

    /**
     * 获取指定前缀的相关属性
     */
    public static Map<String, Object> getMap(Properties props, String prefix) {
        Map<String, Object> kvMap = new LinkedHashMap<String, Object>();
        Set<String> keySet = props.stringPropertyNames();
        if (Collections.isNotEmpty(keySet)) {
            for (String key : keySet) {
                if (key.startsWith(prefix)) {
                    String value = props.getProperty(key);
                    kvMap.put(key, value);
                }
            }
        }
        return kvMap;
    }

}
