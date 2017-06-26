package fridayqun.com.myapplication.common;

/**
 * Created by lenovo on 2016/5/10.
 */
public class Configure {

    //这个就是自己要保管好的私有Key了（切记只能放在自己的后台代码里，不能放在任何可能被看到源代码的客户端程序中）
    // 每次自己Post数据给API的时候都要用这个key来对所有字段进行签名，生成的签名会放在Sign这个字段，API收到Post数据的时候也会用同样的签名算法对Post过来的数据进行签名和验证
    // 收到API的返回的时候也要用这个key来对返回的数据算下签名，跟API的Sign数据进行比较，如果值不一致，有可能数据被第三方给篡改

    public final static int duringtime = 10 * 60 * 1000;

    private static String key = "";

    //微信分配的公众号ID（开通公众号之后可以获取到）
    private static String appID = "";

    //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
    private static String mchID = "";

    public static String getMchID() {
        return mchID;
    }

    public static void setMchID(String mchID) {
        Configure.mchID = mchID;
    }

    public static String getKey() {
        return key;
    }

    public static void setKey(String key) {
        Configure.key = key;
    }

    public static String getAppID() {
        return appID;
    }

    public static void setAppID(String appID) {
        Configure.appID = appID;
    }

    //以下是几个API的路径：
    //1)统一支付接口
    public static String Unifiedorder_API = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    //2)查询订单
    public static String Queryorder_API = "https://api.mch.weixin.qq.com/pay/orderquery";

    //2)关闭订单
    public static String Closeorder_API = "https://api.mch.weixin.qq.com/pay/closeorder";
}
