package fridayqun.com.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;

import fridayqun.com.myapplication.HttpsUtil.AlipayHttpsUtil;
import fridayqun.com.myapplication.common.Configure;
import fridayqun.com.myapplication.common.Create2DCode;
import fridayqun.com.myapplication.common.GetIpAddress;
import fridayqun.com.myapplication.common.HttpsUtil;
import fridayqun.com.myapplication.common.PrecreateTrade;
import fridayqun.com.myapplication.common.QueryTrade;
import fridayqun.com.myapplication.common.RandomStringGenerator;
import fridayqun.com.myapplication.common.XMLParser;
import fridayqun.com.myapplication.impl.ConsumeInstructor;
import fridayqun.com.myapplication.impl.arraytobyte;
import fridayqun.com.myapplication.logutil.MyDate;
import fridayqun.com.myapplication.logutil.MyLog;
import fridayqun.com.myapplication.pay_protocol.PayRequestData;
import fridayqun.com.myapplication.pay_protocol.QueryOrderData;
import fridayqun.com.myapplication.serialImp.SerialControl;
import fridayqun.com.myapplication.serviceutil.ComService;
import fridayqun.com.myapplication.unionPay.UnionPayService;

/**
 * Created by lenovo on 2016/6/6.
 * 弹窗辅助类
 */
public class WindowUtils {

    private static int price;
    public static final long WAITTIME = 60000;
    public static final long firstTime = 8000;

    private static String TAG = WindowUtils.class.getSimpleName();
    private static View mView = null;
    private static WindowManager mWindowManager = null;
    private static Context mContext = null;
    private static final String CANCEL_ACTION = "Cancel_action";
    private static boolean hasChoosen = false;

    public static Boolean isShown = false;
    private TextView txtView;

    //判断是否有网络连接
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            /*
            if it was paied,than it will be true.
            if it was't paied,than it will be false;
             */
            String code_url = bundle.getString("code_url");
            String qr_url = bundle.getString("qr_url");
            String union_code = bundle.getString("union_code");
            //然后要把这个转化成二维码
            ImageView imageView = (ImageView) mView.findViewById(R.id.imageView);
            //把支付宝qr_code转化为二维码
            ImageView alipayView = (ImageView) mView.findViewById(R.id.imageView2);
            //把银联union_code转化为二维码
            ImageView bankView = (ImageView) mView.findViewById(R.id.imageView3);
            try {
                imageView.setImageBitmap(Create2DCode.Create2DCode(code_url));
                alipayView.setImageBitmap(Create2DCode.Create2DCode(qr_url));
                bankView.setImageBitmap(Create2DCode.Create2DCode(union_code));
            } catch (WriterException e) {
                MyLog.d(TAG, "cannot convert to bitmap!");
            }
        }
    };

    Timer t__;
    Handler h__ = new Handler() {
        int count = 60;

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView aa = (TextView) mView.findViewById(R.id.txttime);
            count--;
            aa.setText("支付倒计时  " + count + " ");
            if (count == 0) {
                count = 60;
                t__.cancel();
                Message message = new Message();
                //1代表超时
                message.what = 1;
                myHandler.sendMessage(message);
                hidePopupWindow();
            }
        }
    };

    Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast toast = Toast.makeText(mContext, "超时", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                SerialControl.cancelInsult(ComService.getsPort());
                SerialControl.hasChoosen = false;
                t__.cancel();
                hidePopupWindow();
            }
            if (msg.what == 2) {
                Toast toast = Toast.makeText(mContext, "微信支付成功", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //然后我这边要返回支付成功的信号
                Bundle newBundle = msg.getData();
                SerialControl.consumeInsult(arraytobyte.changetoarraylist(newBundle.getByteArray("Info")), ComService.getsPort());
                SerialControl.hasChoosen = false;
                t__.cancel();
                hidePopupWindow();
            }
            if (msg.what == 3) {
                Toast toast = Toast.makeText(mContext, "支付宝支付成功", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Bundle newBundle = msg.getData();
                SerialControl.consumeInsult(arraytobyte.changetoarraylist(newBundle.getByteArray("Info")), ComService.getsPort());
                SerialControl.hasChoosen = false;
                t__.cancel();
                hidePopupWindow();
            }
            if (msg.what == 4) {
                Toast toast = Toast.makeText(mContext, "银联支付成功", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Bundle newBundle = msg.getData();
                SerialControl.consumeInsult(arraytobyte.changetoarraylist(newBundle.getByteArray("Info")), ComService.getsPort());
                SerialControl.hasChoosen = false;
                t__.cancel();
                hidePopupWindow();
            }
            if (msg.what == 5) {
                Toast toast = Toast.makeText(mContext, "查询失败", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                SerialControl.hasChoosen = false;
                toast.show();
            }
            //得到了6这个消息，要取消。
            if (msg.what == 6) {
                Toast toast = Toast.makeText(mContext, "取消", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                //toast.show();
                t__.cancel();
                hidePopupWindow();
            }
            if (msg.what == 7) {
                Toast toast = Toast.makeText(mContext, "没有联网", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            if (msg.what == 8) {
                Toast toast = Toast.makeText(mContext, "银联交易失败", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    };

    //必须用多线程，因为send是个耗时间的工作
    private Runnable mRunnable = new Runnable() {
        //超时取消没有写
        @Override
        public void run() {
            TimerTask task__ = new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    h__.sendMessage(msg);
                }
            };
            t__ = new Timer(true);
            t__.schedule(task__, 1000, 1000);

            BroadcastReceiver addCancelReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Bundle bundle = intent.getExtras();
                    String aa = bundle.getString("message");
                    if ("6".equals(aa)) {
                        Message mm = new Message();
                        mm.what = 6;
                        myHandler.sendMessage(mm);
                    }
                }
            };

            IntentFilter filter = new IntentFilter();
            filter.addAction(CANCEL_ACTION);
            mContext.registerReceiver(addCancelReceiver, filter);

            if (isNetworkConnected(mContext)) {
                //得到Ip地址
                MyLog.d(TAG, "It has the Connection");
                String Ip = GetIpAddress.getIPAddress(true);
                //UnionPay tradeNo
                String unionPay_tradeNo = '5' + MyDate.outTrantime() + RandomStringGenerator.getRandomIntegerByLength(5);

                //wechat tradeNo
                String my_outTradeNo = '8' + MyDate.outTrantime() + RandomStringGenerator.getRandomIntegerByLength(5);
                //alipay tradeNo
                String Alipay_my_outTradeNo = '9' + MyDate.outTrantime() + RandomStringGenerator.getRandomIntegerByLength(5);

                //wechat pay entity init, 这边是怎么赋值的?                                (device_info,                                                        body,      attach,    outTradeNo,   totalFee,spBillCreateIP,notifyurl,product_id)
                PayRequestData payRequestData = new PayRequestData("NO1125", "煦荣商品", "煦荣商品", my_outTradeNo, price, Ip, Ip, "001");
                //PayRequestData payRequestData = new PayRequestData("myordernumber" + RandomStringGenerator.getRandomStringByLength(10), "煦荣商品", "煦荣商品", my_outTradeNo,1, Ip, Ip, "001");
                //PayRequestData payRequestData = new PayRequestData("myordernumber" + RandomStringGenerator.getRandomStringByLength(10), "旭荣自动售货商品", "旭荣自动售货商品", my_outTradeNo, 1, Ip, Ip, "001");
                //Alipay的创建订单的json格式的content
                //String AlipayInfo = PrecreateTrade.getPrecreateTradeString(Alipay_my_outTradeNo, 0.01, "goods");
                String AlipayInfo = PrecreateTrade.getPrecreateTradeString(Alipay_my_outTradeNo, price / 100.00, "XuRong Goods");
                //String AlipayInfo = PrecreateTrade.getPrecreateTradeString(Alipay_my_outTradeNo, 0.01, "XuRong Good");
                String AlipayResult = null;
                try {
                    AlipayResult = AlipayHttpsUtil.AlipayHttps(AlipayInfo);
                } catch (IOException e) {
                    MyLog.d(TAG, "connect fail!");
                    Message mm = new Message();
                    mm.what = 5;
                    myHandler.sendMessage(mm);
                }
                //wechat创建预支付订单，并获得返回值xml
                String xml = null;
                try {
                    xml = HttpsUtil.sendPost(Configure.Unifiedorder_API, payRequestData);
                } catch (IOException e) {
                    MyLog.d(TAG, "connect fail!");
                    Message mm = new Message();
                    mm.what = 5;
                    myHandler.sendMessage(mm);
                }

                //接下来分别要对两个字符串进行解析alipay parse,此种方法不验证Sign
                //得到qr_code
                String qr_url = "";
                try {
                    MyLog.d(TAG, "AlipayResult:" + AlipayResult);
                    final JSONObject obj = new JSONObject(AlipayResult);
                    qr_url = obj.getJSONObject("alipay_trade_precreate_response").getString("qr_code");
                    MyLog.d(TAG, "the qr_url is" + qr_url);
                } catch (JSONException e) {
                    MyLog.d(TAG, e.getMessage());
                }

                //wechat parse
                Map<String, Object> temp = null;
                try {
                    temp = XMLParser.getMapFromXML(xml);
                } catch (ParserConfigurationException e) {
                    MyLog.d(TAG, "ParserConfigurationException");
                } catch (IOException e) {
                    MyLog.d(TAG, "IOException");
                } catch (SAXException e) {
                    MyLog.d(TAG, "SAXException");
                }

                //UnionPay Response
                String unionPayTime = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date(System.currentTimeMillis()));
                String UnionPayResponse = UnionPayService.getUnionPayRes(unionPay_tradeNo, Integer.toString(price), unionPayTime);
                //UnionPay qrCode
                String union_code = null;
                try {
                    JSONObject jsonObject = new JSONObject(UnionPayResponse);
                    if (jsonObject.get("respCode").equals("00") || jsonObject.get("respMsg").equals("成功[0000000]")) {
                        Log.d(TAG, "银联二维码加载成功");
                        union_code = (String) jsonObject.get("qrCode");
                    } else {
                        union_code = "Request Error;";
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "银联二维码加载失败");
                    union_code = "Request Error;";
                }

                //微信
                Object code_url = temp.get("code_url");
                String tt = code_url.toString();

                Bundle bundle = new Bundle();
                bundle.putString("qr_url", qr_url);//支付宝
                bundle.putString("code_url", tt.toString());//微信
                bundle.putString("union_code", union_code);//银联
                Message message = new Message();
                message.setData(bundle);
                mHandler.sendMessage(message);

                long currentMillin = System.currentTimeMillis();
                int fee = 0;//这个无所谓，只需要订单编号

                //while start
                while (System.currentTimeMillis() < currentMillin + WAITTIME && System.currentTimeMillis() > firstTime) {
                    //wechat query trade status
                    QueryOrderData queryOrderData = new QueryOrderData(my_outTradeNo);
                    String query_alipay = "";
                    String query_xml = "";
                    String query_union = "";
                    if (isNetworkConnected(mContext)) {
                        try {
                            query_xml = HttpsUtil.sendPost(Configure.Queryorder_API, queryOrderData);
                        } catch (IOException e) {
                            MyLog.d(TAG, "connect fail!");
                            Message mm = new Message();
                            mm.what = 5;
                            myHandler.sendMessage(mm);
                        }
                    } else {
                        MyLog.d(TAG, "connect lose");
                        Message mm = new Message();
                        mm.what = 5;
                        myHandler.sendMessage(mm);
                    }
                    if (isNetworkConnected(mContext)) {
                        //支付宝还要查询,先进行支付宝的查询，只要查询trade_status就好了
                        try {
                            query_alipay = AlipayHttpsUtil.AlipayHttps(QueryTrade.getQueryTradeString(Alipay_my_outTradeNo));
                        } catch (IOException e) {
                            Message mm = new Message();
                            mm.what = 5;
                            myHandler.sendMessage(mm);
                        }
                    } else {
                        MyLog.d(TAG, "connect lose");
                    }
                    /************查询银联交易结果，下面进行判断**************************************/
                    if (isNetworkConnected(mContext)) {
                        //银联查询
                        try {
                            query_union = UnionPayService.queryUnionOrder(unionPay_tradeNo, unionPayTime);
                        } catch (Exception e) {
                            Message mm = new Message();
                            mm.what = 5;
                            myHandler.sendMessage(mm);
                        }
                    } else {
                        MyLog.d(TAG, "connect lose");
                    }
    /************************判断银联交易结果****************************************/
                    JSONObject queryUnionTrade = null;
                    String respCode = "";
                    String origRespCode = "";
                    MyLog.d(TAG, "Let's see the union query." + query_union);
                    try {
                        queryUnionTrade = new JSONObject(query_union);
                        respCode = queryUnionTrade.getString("respCode");
                        origRespCode = queryUnionTrade.getString("origRespCode");
                    } catch (JSONException e) {
                        MyLog.d(TAG, "please");
                        MyLog.d(TAG, e.getMessage());
                    }
                    try {
                        if (("00").equals(respCode)) {
                            //查询交易成功
                            if (origRespCode.equals("00")) {
                                MyLog.d(TAG, "查询银联交易成功.");
                                //得到最后要发送的串口号
                                List<Byte> getRetrunNumber = ConsumeInstructor.getReturnInsult(Alipay_my_outTradeNo.toCharArray(), fee);
                                MyLog.d(TAG, getRetrunNumber.toString());
                                Message mes = new Message();
                                mes.what = 4;
                                Bundle mBundle = new Bundle();
                                mBundle.putByteArray("Info", arraytobyte.arraytobyte(getRetrunNumber));
                                mes.setData(mBundle);
                                myHandler.sendMessage(mes);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        MyLog.d(TAG, "Cannot do this query!! equal.");
                    }
/*************************查询银联交易结果结束*************************************************************/
                    //有一种意外情况是交易号不存在
                    String alipay_trade_status = "";
                    JSONObject queryTrade = null;
                    MyLog.d(TAG, "Let's see the query." + query_alipay);
                    try {
                        queryTrade = new JSONObject(query_alipay);
                        alipay_trade_status = queryTrade.getJSONObject("alipay_trade_query_response").getString("trade_status");
                    } catch (JSONException e) {
                        MyLog.d(TAG, "please");
                        MyLog.d(TAG, e.getMessage());
                    }
                    MyLog.d(TAG, "Give me a hint, please!!!");
                    try {
                        if (("TRADE_SUCCESS").equals(alipay_trade_status) || ("TRADE_FINISHED").equals(alipay_trade_status)) {
                            MyLog.d(TAG, "the alipay trade is successful.");
                            //获得消费金额，直接用price来代替了，后面也不用读，不现场
                            //得到最后要发送的串口号
                            List<Byte> getRetrunNumber = ConsumeInstructor.getReturnInsult(Alipay_my_outTradeNo.toCharArray(), fee);
                            MyLog.d(TAG, getRetrunNumber.toString());
                            Message message2 = new Message();
                            message2.what = 3;
                            Bundle mBundle = new Bundle();
                            mBundle.putByteArray("Info", arraytobyte.arraytobyte(getRetrunNumber));
                            message2.setData(mBundle);
                            myHandler.sendMessage(message2);
                            break;
                        }
                    } catch (Exception e) {
                        MyLog.d(TAG, "Cannot do this query!! equal.");
                    }
                    try {
                        Map<String, Object> temp1 = null;
                        try {
                            temp1 = XMLParser.getMapFromXML(query_xml);
                            Object O_return_code = temp1.get("return_code");
                            String return_code = O_return_code.toString();
                            Object O_result_code = temp1.get("result_code");
                            String result_code = O_result_code.toString();
                            Object wechat_pay_no = temp1.get("out_trade_no");
                            String wechat_pay_String = wechat_pay_no.toString();
                            if (("SUCCESS").equals(return_code)) {
                                MyLog.d(TAG, "通信成功");
                                if (("SUCCESS").equals(result_code)) {
                                    MyLog.d(TAG, "查询成功");
                                    Object O_trade_state = temp1.get("trade_state");
                                    String trade_state = O_trade_state.toString();
                                    if (("SUCCESS").equals(trade_state)) {
                                        //获得消费金额
                                        Object O_total_fee = temp1.get("total_fee");
                                        String total_fee = O_total_fee.toString();
                                        fee = Integer.parseInt(total_fee);
                                        MyLog.d(TAG, "fee is " + fee);

                                        //得到最后要发送的串口号
                                        List<Byte> getRetrunNumber = ConsumeInstructor.getReturnInsult(wechat_pay_String.toCharArray(), fee);
                                        MyLog.d(TAG, "getReturnNumber" + getRetrunNumber.toString());

                                        //然后我这边要返回支付成功的信号
                                        //SerialControl.consumeInsult(getRetrunNumber, ComService.getsPort());
                                        Message message1 = new Message();
                                        message1.what = 2;
                                        Bundle mBundle = new Bundle();
                                        mBundle.putByteArray("Info", arraytobyte.arraytobyte(getRetrunNumber));
                                        message1.setData(mBundle);
                                        myHandler.sendMessage(message1);
                                        break;
                                    }
                                }
                            }
                        } catch (ParserConfigurationException e) {
                            MyLog.d(TAG, "ParserConfigurationException");
                        } catch (IOException e) {
                            MyLog.d(TAG, "IOException");
                        } catch (SAXException e) {
                            MyLog.d(TAG, "SAXException");
                        }
                    } catch (Exception e) {
                        MyLog.d(TAG, "something wrong with the map");
                    }


                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        MyLog.d(TAG, "thread sleep fail");
                    }
                }//while end

                QueryOrderData closeOrderData = new QueryOrderData(my_outTradeNo);
                try {
                    HttpsUtil.sendPost(Configure.Closeorder_API, closeOrderData);
                } catch (IOException e) {
                    MyLog.d(TAG, "connect fail!");
                    Message mm = new Message();
                    mm.what = 5;
                    myHandler.sendMessage(mm);
                }
            } else {
                //没有联网，等待60s后退出，发送Msg.what = 5
                Message msg = new Message();
                msg.what = 7;
                myHandler.sendMessage(msg);
            }
            //SerialControl.cancelInsult(ComService.getsPort());
            //when over time close the window
            //hidePopupWindow();//It is successful
        }
    };

    //隐藏弹出框
    public static void hidePopupWindow() {
        MyLog.d(TAG, "hide " + isShown + ", " + mView);
        if (isShown && null != mView) {
            MyLog.d(TAG, "hidePopupWindow");
            mWindowManager.removeView(mView);
            isShown = false;
        }
    }

    private View setUpView(final Context context) {
        MyLog.d(TAG, "setUp view");
        View view = LayoutInflater.from(context).inflate(R.layout.popupwindow, null);
        return view;
    }

    /**
     * 显示弹处框
     */
    public void showPopupWindow(final Context context, final int price, final ArrayList<Byte> curSeq) {
        if (isShown) {
            MyLog.d(TAG, "return cause already shown.");
            return;
        }
        isShown = true;
        MyLog.d(TAG, "showPopupWindow");
        //获取应用的Context
        mContext = context.getApplicationContext();
        //获取WindowManager
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mView = setUpView(context);
        txtView = (TextView) mView.findViewById(R.id.txttime);
        final LayoutParams params = new LayoutParams();
        //类型,shown on top of all other apps
        params.type = LayoutParams.TYPE_SYSTEM_ALERT;
        // 设置flag
        int flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        // | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
        params.flags = flags;
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT;
        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
        // 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
        // 不设置这个flag的话，home页的划屏会有问题
        params.gravity = Gravity.CENTER;
        params.softInputMode = LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
        params.systemUiVisibility = LayoutParams.FLAG_FULLSCREEN;
        params.systemUiVisibility = LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.systemUiVisibility = LayoutParams.FLAG_NOT_TOUCHABLE;
        params.systemUiVisibility = LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.systemUiVisibility = LayoutParams.FLAG_NOT_FOCUSABLE;
        params.systemUiVisibility = LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.systemUiVisibility = LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_VISIBLE;
        mWindowManager.addView(mView, params);
        this.price = price;
        new Thread(mRunnable).start();
        MyLog.i(TAG, "add view");
    }
}
