package fridayqun.com.myapplication.impl;

import fridayqun.com.myapplication.logutil.MyLog;

/**
 * Created by friday on 2016/3/1.
 */
public class analyticalImpl{

    //获得交易码的后十位
    public static int[] getTenChar(String transaction_id){

        int[] result = new int[18];
        int length = transaction_id.length();

        if (length < 18){

            for(int i = 0; i < length; i++){
                result[i] = transaction_id.charAt(i) - '0';
            }
            for(int i = length; i < 20; i++){
                result[i] = 0;
            }

        }else {

            String temp = transaction_id.substring(length - 18, length);
            for(int i = 0; i < 18; i++){
                result[i] = temp.charAt(i) - '0';
            }

        }

        return result;

    }

//------------------------------------------------------------------------------
// 形式： float PriceHextoDec(byte[] Price, int PriceSize)
// 参数： byte[] Price表示金额的16进制字节，int PriceSize 字节长度
// 调用者：串口通信类中解析java.lang.Object75投币指令时解析金额
// 后置：
// 返回值： float fPrice，返回价格
//------------------------------------------------------------------------------
    //投币金额解析。十六进制转十进制
    public static int PriceHextoDec(byte[] chPrice, int PriceSize) {

        int ResultPrice = 0;

        for(int i = 0; i < PriceSize; i++){

            ResultPrice += ((Byte)chPrice[i]) << (8*(PriceSize-1-i));

        }
        //fPrice = lPrice / 100;
        return ResultPrice;
    }

    //BCDtoDec
    public static int BCDtoDec(byte[] Result, int Size){

        int result = 0;
        int weight = 1;

        for(int i = Size - 1; i >= 0; i--){

            result += (int) (Result[i] & 0x0000000f) * weight;
            weight *= 10;

            result += (int) ((Result[i] & 0x000000f0)>>>4) * weight;
            weight *= 10;

        }

        MyLog.d("得到金额，返回int", result);
        return result;

    }

}
