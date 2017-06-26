package fridayqun.com.myapplication.serviceutil;

/**
 * Created by lenovo on 2016/5/17.
 */
public class AnalyticalImpl {

    //将16进制转换成10进制
    public static int HextoDec(byte[] HexData, int DataSize) {

        int DecResult = 0;

        for(int i = 0; i < DataSize; i++){

            DecResult += (long)((Byte)HexData[i]) << (8*(DataSize-1-i));

        }
        return DecResult;
    }

}
