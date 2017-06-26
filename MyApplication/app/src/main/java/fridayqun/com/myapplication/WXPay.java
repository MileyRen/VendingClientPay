package fridayqun.com.myapplication;



/**
 * Created by fridayqun on 2016/5/10.
 */

import fridayqun.com.myapplication.common.Configure;

/**
 * SDK总入口
 */
public class WXPay {

    /**
     * 初始化SDK依赖的几个关键配置
     * @param key 签名算法需要用到的秘钥
     * @param appID 公众账号ID
     * @param mchID 商户ID
     */
    public static void initSDKConfiguration(String key,String appID,String mchID){

        Configure.setKey(key);
        Configure.setAppID(appID);
        Configure.setMchID(mchID);

    }

}
