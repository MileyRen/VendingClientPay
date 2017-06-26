package fridayqun.com.myapplication.impl;

import java.util.ArrayList;
import java.util.List;

import fridayqun.com.myapplication.logutil.MyLog;

/**
 * Created by lenovo on 2016/5/27.
 */
public class ConsumeInstructor {

    public final static byte start = 0x02;
    public final static byte end = 0x03;

    //这个长度最后计算
    //public final static byte[] datalength = {0x00, 0x00, 0x00, (byte)0xe5};

    public final static byte type = (byte)0x80;

    //这个也是最后添，然后减10
    //public final static byte[] length = {0x00, 0x00, 0x00, (byte)0xDB};

    public final static byte slot = 0x00;
    public final static byte bseq = 0x00;
    public final static byte bBwi = 0x01;
    public final static byte[] level_param = {0x00, 0x00};

    //表示支付完成
    public final static byte[] finish_deal = {(byte)0xd0,
            0x01, 0x03};

    public final static byte[] length_2 = {(byte)0xd1, (byte)0x81, (byte)0xe7};

    public final static byte[] code_8583 = {0x01, 0x02, 0x60, 0x00, 0x26, 0x00, 0x00, 0x60, 0x31, 0x00, 0x31, 0x10,
            0x61, 0x02, 0x00, 0x70, 0x24, 0x06, (byte)0x80, (byte)0x00, (byte)0xC0, (byte)0x82, 0x13 };

    public final static byte[] mm = {0x00, 0x00, 0x42, 0x07, 0x20, 0x00, 0x00, 0x00,
            0x36, 0x39, 0x30, 0x36, 0x30, 0x30,0x32, 0x35, 0x33, 0x30, 0x39, 0x33, 0x31,
            0x30, 0x30, 0x35, 0x33, 0x33, 0x31,0x36, 0x39, 0x30, 0x36, 0x31, 0x35, 0x36, 0x00, 0x00};

    public final static byte[] state = {(byte)0x90, 0x00};

    public static List<Byte> orderToBCB(char[] order, int length){

        List<Byte> result = new ArrayList<>();

        for(int i = 0; i < length; i++){

            byte a = (byte)(order[i] & 0x0F);
            MyLog.d("change to BCD a",a);
            byte b = (byte)(order[++i] & 0x0F);
            MyLog.d("change to BCD b",b);
            byte c = (byte) ((a<<4) ^ b);
            MyLog.d("change to BCD c",c);

            result.add(c);
        }
    return result;
    }

    public static List<Byte> orderBCD(int[] order, int length){

        List<Byte> result = new ArrayList<>();

        for(int i = 0; i < length; i++){
            byte a = (byte)(order[i] & 0x0F);
            MyLog.d("change to BCD a",a);
            byte b = (byte)(order[++i] & 0x0F);
            MyLog.d("change to BCD b",b);
            byte c = (byte) ((a<<4) ^ b);
            MyLog.d("change to BCD c",c);

            result.add(c);
        }

        MyLog.d("return " , result.toString());
      return result;
    }

    public static ArrayList<Byte> changetoarraylist(byte[] tt){

        ArrayList<Byte> result = new ArrayList<Byte>();
        for(int i = 0; i < tt.length; i++){

            result.add(tt[i]);

        }
        return result;
    }

    public static byte getChecksum(ArrayList<Byte> content) {

        byte checksum = 0x00;

        for(int i = 0; i < content.size() ; i++){
            checksum ^= content.get(i);
        }
        return checksum;
    }

    //得到order的HEX值
    public static List<Byte> getOrderHex(int[] wx_order){

        List<Byte> result = new ArrayList<>();

        for(int i : wx_order){
            result.add((byte)(i & 0x0F));
        }

        return result;
    }

    public static byte[] intToByteArray(int i){
        byte[] result = new byte[4];
        //由高位到低位
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }

    public static List<Byte> getReturnInsult(char[] wx_order_number, int price) {

        ArrayList<Byte> result = new ArrayList<Byte>();
        byte[] pricebyte = intToByteArray(price);

        //先算05的那个
        result.addAll(changetoarraylist(finish_deal));
        result.addAll(changetoarraylist(length_2));
        result.addAll(changetoarraylist(code_8583));
        byte length = 0x20;
        result.add(length);
        //result.addAll(orderBCD(wx_order_number, wx_order_number.length));
        //添加订单号码,自定义20位char[],转换为BCD
        result.addAll(orderToBCB(wx_order_number, wx_order_number.length));
        result.add((byte) 0x00);

        result.add((byte)0x00);
        result.add((byte)0x00);
        result.addAll(arraytobyte.changetoarraylist(pricebyte));

        result.addAll(changetoarraylist(mm));

        result.addAll(changetoarraylist(state));

        //第一个长度
        int length_in = result.size();
        byte[] length_in_byte = intToByteArray(length_in);

        ArrayList<Byte> afterResult = changetoarraylist(length_in_byte);

        afterResult.add(slot);
        afterResult.add(bseq);
        afterResult.add(bBwi);
        afterResult.addAll(changetoarraylist(level_param));

        afterResult.addAll(result);

        //0x80
        afterResult.add(0, (byte)0x80);

        //length_out
        byte[] length_out_byte = intToByteArray(afterResult.size());
        ArrayList<Byte> finalResult = changetoarraylist(length_out_byte);
        finalResult.addAll(afterResult);
        byte checkout = getChecksum(finalResult);
        finalResult.add(checkout);
        finalResult.add(0, start);
        finalResult.add(end);

        for(byte i : finalResult){
            MyLog.d("ttttttttttts", Integer.toHexString(i & 0x00ff) + " ");
        }
        return finalResult;
    }
}
