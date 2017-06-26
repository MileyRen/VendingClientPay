package fridayqun.com.myapplication.logutil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lenovo on 2016/3/18.
 */
public class MyDate {

    public static String getFileName(){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(System.currentTimeMillis()));
        return date;
    }
    public static String getDataEn(){
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = format1.format(new Date(System.currentTimeMillis()));
        return date1;
    }

    public static String outTrantime(){
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");
        String date2 = format2.format(new Date(System.currentTimeMillis()));
        return date2;
     }
}
