package fridayqun.com.myapplication.common;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by lenovo on 2016/5/14.
 */
public class HttpsUtil {

    private static String TAG = HttpsUtil.class.getSimpleName();

    public static String sendPost(String sendurl, Object xmlObj) throws IOException {

        String result = "";

        //解决XStream对出现双下划线的bug
        XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));

        //将要提交给API的数据对象转换成XML格式数据Post给API
        String postDataXML = xStreamForRequestPostData.toXML(xmlObj);

        //MyLog.i(TAG, "API，POST过去的数据是：");
        //MyLog.i(TAG, postDataXML);

            URL url = new URL(sendurl);
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            ((HttpsURLConnection)connection).setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();

            //把封装好的实体数据发送到输出流
            OutputStream outputStream = connection.getOutputStream();
            byte[] bytes = postDataXML.toString().getBytes();
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();

            //服务器返回输入流并读写
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = in.readLine()) != null){
                result += line;
            }
            in.close();
            //MyLog.i(TAG, result);
        return result;
    }

}
