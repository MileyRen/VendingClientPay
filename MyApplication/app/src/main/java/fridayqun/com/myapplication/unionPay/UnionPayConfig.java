package fridayqun.com.myapplication.unionPay;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Miley_Ren on 2017/6/20.
 */

public class UnionPayConfig {
    public static final String encoding = "UTF-8";      //编码
    /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
    public static final String version = "5.1.0";       //版本号，固定填写
    public static final String signMethod = "01";       //签名方法
    public static final String txnType = "01";          //交易类型01：消费
    public static final String txnSubType = "07";       //交易子类 07：申请消费二维码
    public static final String bizType = "000000";      //产品类型
    public static final String channelType = "08";      //渠道类型08：手机
    /***商户接入参数***/
    public static final String accessType = "0";        //接入类型，商户接入填0 ，不需修改（0：直连商户， 1： 收单机构 2：平台商户）
    public static final String currencyCode = "156";    //境内商户固定 156 人民币

    /**以下为真实数据**/
    public static final String merId = "301310053310155"; //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
    public static final String termId = "NO1125";//终端号，可以没有
    //public static final String payTimeOut ="";//支付超时时间
    //public static final String txnTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));//订单发送时间，取系统时间，格式为yyyyMMddHHmmss，必须取当前时间，否则会报txnTime无效
    //public static final String orderId = txnTime;//商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
    public static final String txnAmt = "120";             //交易金额 单位为分，不能带小数点
    public static final String backUrl = "http://222.222.222.222:8080/ACPSample_AppServer/backRcvResponse"; //交易通知地址
    public static int timeOut = 2 * 60000;//超时时间2分钟

    public static final String UnionPayLocalIp = "172.30.214.78";//"172.29.187.30";//"49.52.10.180";
    public static final String UnionPayLocalPort = "8080";
    public static final String UnionPayLocalUrl = "http://" + UnionPayLocalIp + ":" + UnionPayLocalPort + "/ACPSample_AppServer/form05_6_1_ApplyQrCode";
    public static final String UnionPayQueryUrl = "http://" + UnionPayLocalIp + ":" + UnionPayLocalPort + "/ACPSample_AppServer/form05_6_3_Query";
}
