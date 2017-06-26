package fridayqun.com.myapplication.HttpsUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import fridayqun.com.myapplication.common.AlipayConfigure;
import fridayqun.com.myapplication.logutil.MyLog;

/**
 * Created by lenovo on 2016/6/7.
 */
public class AlipayHttpsUtil {

    private static String TAG = AlipayHttpsUtil.class.getSimpleName();

    public static String AlipayHttps(String send) throws IOException {

        String result = "";


            URL url = new URL(AlipayConfigure.URL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Contect-Type", "text/xml;charset=UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();

            OutputStream outputStream = connection.getOutputStream();
            byte[] bytes = send.getBytes();

            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = in.readLine()) != null) {
                result += line;
            }

            in.close();

            MyLog.d(TAG, result.getBytes("utf-8"));

        return result;

    }
}
