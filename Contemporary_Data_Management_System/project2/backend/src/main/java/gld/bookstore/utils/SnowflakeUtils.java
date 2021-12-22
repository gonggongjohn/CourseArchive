package gld.bookstore.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class SnowflakeUtils {
    private static final Snowflake snowflake= IdUtil.createSnowflake(0,1);

    public static String getId(){
        return String.valueOf(snowflake.nextId());
    }
}
