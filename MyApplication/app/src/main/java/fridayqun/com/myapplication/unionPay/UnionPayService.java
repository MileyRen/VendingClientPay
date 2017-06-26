package fridayqun.com.myapplication.unionPay;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import static fridayqun.com.myapplication.unionPay.UnionPayConfig.UnionPayLocalUrl;
import static fridayqun.com.myapplication.unionPay.UnionPayConfig.UnionPayQueryUrl;
import static fridayqun.com.myapplication.unionPay.UnionPayConfig.merId;
/**
 * Created by Miley_Ren on 2017/6/23.
 */

public class UnionPayService {
    public static String getUnionPayRes(String orderId, String txnAmt, String txnTime) {
        String result = null;
        Map<String, String> contentData = new HashMap<String, String>();
        /***商户接入参数***/
        contentData.put("merId", merId);            //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        contentData.put("orderId", orderId);         //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        contentData.put("txnTime", txnTime);            //订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        contentData.put("txnAmt", txnAmt);            //交易金额 单位为分，不能带小数点
        try {
            result = HttpURLConnUtil.postByResponse(UnionPayLocalUrl, contentData, "utf-8");
            Log.d("返回报文", result);
        } catch (Exception e) {
            Log.d("UnionPayService", "二维码请求失败");
           result = "";
        }
        return result;
    }

    public static String queryUnionOrder(String orderId, String txnTime){
        String queryResult = null;
        Map<String, String> unionRequest = new HashMap<String, String>();
        unionRequest.put("merId", merId);
        unionRequest.put("txnTime", txnTime);
        unionRequest.put("orderId", orderId);
        try {
            queryResult = HttpURLConnUtil.postByResponse(UnionPayQueryUrl, unionRequest, "utf-8");
        } catch (Exception e) {
            Log.d("UnionPayService", "查询失败");
            queryResult = null;
        }
        return queryResult;
    }
}
