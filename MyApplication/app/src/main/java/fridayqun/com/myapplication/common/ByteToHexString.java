package fridayqun.com.myapplication.common;

import java.util.ArrayList;

/**
 * Created by lenovo on 2016/5/26.
 */
public class ByteToHexString {

    public static String turnBytetoString (ArrayList<Byte> input){

        String result = "";

        for(Byte b : input){
            result += Integer.toHexString((int)b);
        }

        return result;
    }
}
