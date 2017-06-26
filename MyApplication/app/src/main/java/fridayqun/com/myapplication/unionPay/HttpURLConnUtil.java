package fridayqun.com.myapplication.unionPay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by Miley_Ren on 2017/6/21.
 */

public class HttpURLConnUtil {
    static HttpURLConnection httpURLConnection = null;
    public static String getByResponse(final String path) throws Exception {
        FutureTask<String> task = new FutureTask<String>(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return getRequest(path);
                    }
                }
        );
        new Thread(task).start();
        return task.get();
    }

    //使用FutureTask可以有返回值
    public static String postByResponse(final String path, final Map<String, String> params, final String encode) throws Exception {
        FutureTask<String> task = new FutureTask<String>(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        String response = postRequest(path,params,encode);
                        return response;
                    }
                }
        );
        new Thread(task).start();
        return task.get();
    }
    /*
     * Function：发送get请求到服务器
     * Params: path请求url */
    public static String getRequest(String path ){
        String response= null;
        try {
            URL url = new URL(path);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(8000);
            httpURLConnection.setReadTimeout(8000);
            InputStream in = httpURLConnection.getInputStream();
            response= dealResponseResult(in);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(httpURLConnection!=null){
                httpURLConnection.disconnect();
            }
        }
        return response;
    }

    /*
     * Function  :   发送Post请求到服务器
     * Param     :   path请求url, params请求体内容，encode编码格式
     */
    public  static String postRequest(String path, Map<String, String> params, String encode) {
        byte[] data = getRequestData(params, encode).toString().getBytes(); //获得请求体
        try {
            URL url = new URL(path);
            httpURLConnection =  (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(8000);        //设置连接超时时间
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");   //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);
            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                String res_msg = dealResponseResult(inputStream);
                return res_msg;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(httpURLConnection !=null){
                httpURLConnection.disconnect();
            }
        }
        return "";
    }

    /*
     * Function  :   封装请求体信息
     * Param     :   params请求体内容，encode编码格式
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
    /*
     * Function  :   处理服务器的响应结果（将输入流转化成字符串）
     * Param     :   inputStream服务器的响应输入流
     */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }
}
