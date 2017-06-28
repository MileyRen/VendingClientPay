package fridayqun.com.myapplication.UnionPayService;

/**
 * Created by Miley_Ren on 2017/6/20.
 */

public class UnionPayConfig {
    public static final String encoding = "UTF-8";      //编码
    /***
     * 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改
     ***/
    public static final String version = "5.1.0";       //版本号，固定填写
    public static final String signMethod = "01";       //签名方法
    public static final String txnType = "01";          //交易类型01：消费
    public static final String txnType_query = "00";          //交易类型 00-默认
    public static final String txnSubType = "07";       //交易子类 07：申请消费二维码
    public static final String txnSubType_query = "00";       //交易子类 00：默认
    public static final String bizType = "000000";      //产品类型
    public static final String bizTypp_query = "000201";      //业务类型
    public static final String channelType = "08";      //渠道类型08：手机
    /***
     * 商户接入参数
     ***/
    public static final String accessType = "0";        //接入类型，商户接入填0 ，不需修改（0：直连商户， 1： 收单机构 2：平台商户）
    public static final String currencyCode = "156";    //境内商户固定 156 人民币
    public static final String merId = "301310053310155"; //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
    public static final String termId = "NO1125";//终端号
    public static final String backUrl = "http://222.222.222.222:8080/ACPSample_AppServer/backRcvResponse";
    public static final String backRequestUrl = "https://gateway.95516.com/gateway/api/backTransReq.do";
    public static final String singleQueryUrl="https://gateway.95516.com/gateway/api/queryTrans.do";
    public static final String signCertPwd = "111111";
    public static final String signCertType= "PKCS12";
    public static final String signCertPath ="key.pfx";
    public static final String middleCertPath = "acp_prod_middle.cer";
    public static final String rootCertPath = "acp_prod_root.cer";
    public static final String encryptCertPath = "acp_prod_enc.cer";
    public static final int UNION_TIMEOUT =1 * 60000;//超时时间,1分钟
}