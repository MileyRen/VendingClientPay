package fridayqun.com.myapplication.UnionPayService;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import static fridayqun.com.myapplication.UnionPayService.UnionPayConfig.*;

/**
 * Created by Miley_Ren on 2017/6/28.
 */

public class UnionServiceInterface {

    /***
     * 获取银联二维码
     *
     * @param orderId    订单号
     * @param txnAmt     订单发送时间
     * @param payTimeout 订单超时时间
     * @return 返回银联二维码
     **/
    public static String getQr_Code(String orderId, String txnAmt, String txnTime, String payTimeout) {
        String qrCode = null;
        Map<String, String> contentData = new HashMap<String, String>();
        /*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
        contentData.put("version", version); // 版本号 全渠道默认值
        contentData.put("encoding", encoding); // 字符集编码
        contentData.put("signMethod", signMethod); // 签名方法
        contentData.put("txnType", txnType); // 交易类型 01:消费
        contentData.put("txnSubType", txnSubType); // 交易子类 07：申请消费二维码
        contentData.put("bizType", bizType); // 填写000000
        contentData.put("channelType", channelType); // 渠道类型 08手机
        /*** 商户接入参数 ***/
        contentData.put("merId", merId); // 商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        contentData.put("accessType", accessType);
        contentData.put("currencyCode", currencyCode); // 境内商户固定 156 人民币
        contentData.put("termId", termId);//终端号
        contentData.put("txnAmt", txnAmt); // 交易金额 单位为分，不能带小数点
        contentData.put("orderId", orderId); // 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        contentData.put("payTimeout", payTimeout);//订单超时时间
        contentData.put("txnTime", txnTime); // 订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        contentData.put("backUrl", backUrl);
        Map<String, String> reqData = AcpService.sign(contentData, encoding); // 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        try {
            qrCode = getQrCodePost(reqData, backRequestUrl, encoding); // 发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
            Log.d("qrCode", qrCode);
        } catch (Exception e) {
            Log.d("UnionServiceInterface", "获取银联二维码失败");
            qrCode = null;
        }
        return qrCode;
    }

    /**
     * 查询银联交易结果
     *
     * @param orderId 订单ID
     * @param txnTime 订单发送时间
     * @return 返回origRespCode，只有为00时，查询订单结果是正确的
     **/
    public static String queryUnionOrder(String orderId, String txnTime) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("version", version); // 版本号
        data.put("encoding", encoding); // 字符集编码 可以使用UTF-8,GBK两种方式
        data.put("signMethod", signMethod); // 签名方法
        data.put("txnType", txnType_query); // 交易类型 00-默认
        data.put("txnSubType", txnSubType_query); // 交易子类型 默认00
        data.put("bizType", bizTypp_query); // 业务类型
        /*** 商户接入参数 ***/
        data.put("merId", merId); // 商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        data.put("accessType", accessType); // 接入类型，商户接入固定填0，不需修改
        /*** 要调通交易以下字段必须修改 ***/
        data.put("orderId", orderId); // ****商户订单号，每次发交易测试需修改为被查询的交易的订单号
        data.put("txnTime", txnTime); // ****订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间
        /** 请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文-------------> **/
        Map<String, String> reqData = AcpService.sign(data, encoding); // 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String origRespCode = null;
        try {
            origRespCode = getQueryPost(reqData, singleQueryUrl, encoding);
        } catch (Exception e) {
            Log.d("UnionServiceInterface","订单查询失败");
        }
        return origRespCode;
    }

    public static String getQrCodePost(final Map<String, String> params, final String path, final String encode) throws Exception {
        FutureTask<String> task = new FutureTask<String>(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        String qrCode = null;
                        Map<String, String> rspData = AcpService.post(params, path, encode);
                        if (!rspData.isEmpty()) {
                            if (AcpService.validate(rspData, encoding)) {
                                Log.d("MainActivity", "验证签名成功");
                                String respCode = rspData.get("respCode");
                                if (respCode.equals("00")) {
                                    qrCode = rspData.get("qrCode");
                                }
                            }
                        }
                        return qrCode;
                    }
                }
        );
        new Thread(task).start();
        return task.get();
    }

    public static String getQueryPost(final Map<String, String> params, final String path, final String encode) throws Exception {
        FutureTask<String> task = new FutureTask<String>(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        String origRespCode = null;
                        Map<String, String> rspData = AcpService.post(params, path, encode);
                        if (!rspData.isEmpty()) {
                            if (AcpService.validate(rspData, encoding)) {
                                if (("00").equals(rspData.get("respCode"))) {// 如果查询交易成功
                                    origRespCode = rspData.get("origRespCode");
                                }
                            } else {
                                Log.w("UnionServiceInterface", "验证签名失败");
                            }
                        }
                        return origRespCode;
                    }
                }
        );
        new Thread(task).start();
        return task.get();
    }
}
