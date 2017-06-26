package fridayqun.com.myapplication.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/3/14.
 */
public class arraytobyte {

    public static byte[] arraytobyte(List<Byte> input){
        int length = input.size();
        byte[] output = new byte[length];
        for(int i = 0; i < input.size(); i++){
            output[i] = input.get(i);
        }
        return output;
    }

    public static ArrayList<Byte> changetoarraylist(byte[] tt){

        ArrayList<Byte> result = new ArrayList<Byte>();
        for(int i = 0; i < tt.length; i++){

            result.add(tt[i]);

        }
        return result;
    }
}
