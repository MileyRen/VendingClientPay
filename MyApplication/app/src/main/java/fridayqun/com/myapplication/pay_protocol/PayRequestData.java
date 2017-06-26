package fridayqun.com.myapplication.pay_protocol;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fridayqun.com.myapplication.common.Configure;
import fridayqun.com.myapplication.common.RandomStringGenerator;
import fridayqun.com.myapplication.common.Signature;

/**
 * Created by lenovo on 2016/5/10.
 */
public class PayRequestData {

    //private static String TAG = PayRequestData.class.getSimpleName();

    //每个字段具体的意思请查看API文档
    private String appid = "";
    private String mch_id = "";
    private String device_info = "";
    private String nonce_str = "";
    private String sign = "";
    private String body = "";
    private String attach = "";
    private String out_trade_no = "";
    private int total_fee = 0;
    private String spbill_create_ip = "";
    private String time_start = "";
    private String time_expire = "";
    private String goods_tag = "";

    private String notify_url = "";
    private String trade_type = "NATIVE";
    private String product_id = "";

    //getter and setter


    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public int getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(int total_fee) {
        this.total_fee = total_fee;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_expire() {
        return time_expire;
    }

    public void setTime_expire(String time_expire) {
        this.time_expire = time_expire;
    }

    public String getGoods_tag() {
        return goods_tag;
    }

    public void setGoods_tag(String goods_tag) {
        this.goods_tag = goods_tag;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    /**
     * @param body 要支付的商品的描述信息，用户会在支付成功页面里看到这个信息
     * @param attach 支付订单里面可以填的附加数据，API会将提交的这个附加数据原样返回
     * @param outTradeNo 商户系统内部的订单号,32个字符内可包含字母, 确保在商户系统唯一
     * @param totalFee 订单总金额，单位为“分”，只能整数
     * @param device_info 商户自己定义的扫码支付终端设备号，方便追溯这笔交易发生在哪台终端设备上
     * @param spBillCreateIP 订单生成的机器IP
     */
    public PayRequestData(String device_info,
                          String body,
                          String attach,
                          String outTradeNo,
                          int totalFee,
                          String spBillCreateIP,
                          String notifyurl,
                          String product_id){

        //微信分配的公众号ID（开通公众号之后可以获取到）
        setAppid(Configure.getAppID());//1

        //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
        setMch_id(Configure.getMchID());//2

        //微信支付分配的终端设备号
        //设置微信支付分配的终端设备号，商户自定义
        //可以没有
        setDevice_info(device_info);//3

        //随机字符串，不长于32 位
        setNonce_str(RandomStringGenerator.getRandomStringByLength(32));//4

        //要支付的商品的描述信息，用户会在支付成功页面里看到这个信息
        setBody(body);//5

        //支付订单里面可以填的附加数据，API会将提交的这个附加数据原样返回，有助于商户自己可以注明该笔消费的具体内容，方便后续的运营和记录
        setAttach(attach);//6

        //商户系统内部的订单号,32个字符内可包含字母, 确保在商户系统唯一
        setOut_trade_no(outTradeNo);//7

        //订单总金额，单位为“分”，只能整数
        setTotal_fee(totalFee);//8

        //订单生成的机器IP
        setSpbill_create_ip(spBillCreateIP);//9

        //订单生成时间， 格式为yyyyMMddHHmmss，如2009年12 月25 日9 点10 分10 秒表示为20091225091010。时区为GMT+8 beijing。该时间取自商户服务器
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStart = df.format(new Date());
        setTime_start(timeStart);

        //订单失效时间，格式同上,采用10min后失效
        //1、获取当前时间，获取到的时间类型是long类型的，单位是毫秒
        long currentTime = System.currentTimeMillis();
        //2、在这个基础上加上10分钟：
        currentTime = currentTime + Configure.duringtime;
        //3、格式化时间，获取到的就是当前时间10分钟之后的时间
        Date finishtime = new Date(currentTime);
        String ftime = df.format(finishtime);
        setTime_expire(ftime);

        //接收微信支付成功通知
        setNotify_url(notifyurl);

        setProduct_id(product_id);

        //根据API给的签名规则进行签名
        String sign = null;
        sign = Signature.getSign(toMap());
        setSign(sign);//把签名数据设置到Sign这个属性中//4
    }

    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<String, Object>();

        //getDeclaredFields()返回Class中所有的字段，包括私有字段
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object obj;
            try {
                obj = field.get(this);
                if(obj!=null){
                    map.put(field.getName(), obj);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

}
