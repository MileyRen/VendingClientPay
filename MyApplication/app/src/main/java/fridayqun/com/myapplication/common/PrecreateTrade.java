package fridayqun.com.myapplication.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import fridayqun.com.myapplication.logutil.MyLog;

/**
 * Created by lenovo on 2016/6/3.
 */
public class PrecreateTrade {

    private static String TAG = PrecreateTrade.class.getSimpleName();

    public static String getPrecreateTradeString(String out_trade_no, double total_amount, String subject){

        String app_id = AlipayConfigure.APPID;
        String method = "alipay.trade.precreate";
        String charset = "utf-8";
        String sign_type = "RSA";
        String terminal_id = "NO1125";
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));

        String orderInfo = "app_id=" + app_id + "&";
        orderInfo = orderInfo + "biz_content={" + "\"" + "out_trade_no" + "\"" + ":" + "\"" + out_trade_no + "\"" +
                "," + "\"" + "subject" + "\"" + ":" + "\"" + subject + "\"" + "," +
                "\"" + "timeout_express" + "\"" + ":" + "\"" + "1m" + "\"" + "," +
                "\"" + "total_amount" + "\"" + ":" + "\"" + total_amount + "\"" +
                "," + "\"" + "terminal_id" + "\"" + ":" + "\"" + terminal_id + "\"" + "}";

        orderInfo = orderInfo + "&charset=" + charset + "&method=" + method +
                "&sign_type=" + sign_type + "&timestamp=" + timestamp + "&version=1.0";

        MyLog.d(TAG, orderInfo);

        String sign = SignUtils.sign(orderInfo, AlipayConfigure.RSA_RRIVATE_KEY);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String result = orderInfo + "&sign=" + sign;

        MyLog.d(TAG, result);

        return result;
    }

}
