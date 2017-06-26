package fridayqun.com.myapplication.pay_protocol;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import fridayqun.com.myapplication.common.Configure;
import fridayqun.com.myapplication.common.RandomStringGenerator;
import fridayqun.com.myapplication.common.Signature;

/**
 * Created by lenovo on 2016/5/26.
 */
public class QueryOrderData {

    private String appid = "";
    private String mch_id = "";
    private String out_trade_no = "";
    private String nonce_str = "";
    private String sign = "";

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

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
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

    public QueryOrderData(String out_trade_no){

        //微信分配的公众号ID（开通公众号之后可以获取到）
        setAppid(Configure.getAppID());//1

        //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
        setMch_id(Configure.getMchID());//2

        setOut_trade_no(out_trade_no);//3

        //随机字符串，不长于32 位
        setNonce_str(RandomStringGenerator.getRandomStringByLength(32));//4

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
