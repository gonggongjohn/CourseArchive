package gld.bookstore.utils;

public class IHttpStatus {
    // 用户不存在
    public static final int NON_EXIST_USER = 511;
    // 用户已存在
    public static final int EXISTED_USER = 512;
    // 商铺不存在
    public static final int NON_EXIST_STORE = 513;
    // 商铺已存在
    public static final int EXISTED_STORE = 514;
    // 商品不存在
    public static final int NON_EXIST_BOOK = 515;
    // 商品已存在
    public static final int EXISTED_BOOK = 516;
    // 库存不足
    public static final int LOW_STOCK_LEVEL = 517;
    // 无效订单
    public static final int INVALID_ORDER = 518;
    // 余额不足
    public static final int LOW_MONEY = 519;
    // 用户错误
    public static final int WRONG_USER = 520;

    // 无效参数
    public static final int INVALID_PARAM = 551;
}
