package fridayqun.com.myapplication.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lenovo on 2016/5/10.
 */
public class PayCommonUtil {

    /**
    * 获取当前时间 yyyyMMddHHmmss
    *
    * @return String
    */
    public static String getCurrTime(){
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = outFormat.format(now);
        return s;
    }
}
