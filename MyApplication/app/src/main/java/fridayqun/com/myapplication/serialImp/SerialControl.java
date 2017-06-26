package fridayqun.com.myapplication.serialImp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import fridayqun.com.myapplication.impl.analyticalImpl;
import fridayqun.com.myapplication.impl.arraytobyte;
import fridayqun.com.myapplication.logutil.MyLog;
import fridayqun.com.myapplication.serviceutil.AnalyticalImpl;
import fridayqun.com.myapplication.util.FT311UARTInterface;

public class SerialControl {

    public static String TAG = SerialControl.class.getSimpleName();
    private static final String BROADCAST_ACTION = "Price_curSeq";
    public static int IsQuery = 0;
    private static ArrayList<Byte> PreSeq = null;
    private static final String CANCEL_ACTION = "Cancel_action";
    public static boolean hasChoosen = false;

    final static byte[] CANCELORDER = {0x02, 0x00, 0x00, 0x00,
            0x0c, (byte) 0x80, 0x00, 0x00, 0x00, 0x02,  0x00, 0x05,
            0x3c, 0x00, 0x00, (byte) 0x90, 0x00, 0x27, 0x03};

    final static byte[] OVERORDER = {0x02, 0x00, 0x00, 0x00, 0x0C, (byte) 0x80, 0x00,
            0x00, 0x00, 0x02, 0x00, 0x00, 0x01, 0x00, 0x00, (byte) 0xBE, (byte) 0xFB,
            (byte) 0xCA, 0x03};

    //校验和获取校验是一样的。
//------------------------------------------------------------------------------
// 形式： boolean SerialControl::checkData(ArrayList inData)
// 参数： ArrayList<Byte> inData <in>,串口读取的字节串
// 调用者：检查字节串是否正确
// 后置：
// 返回值： true，正确；false，错误
//------------------------------------------------------------------------------
    public static boolean checkData(ArrayList<Byte> inData) {

        byte checksum = 0x00;
        int i;
        for(i = 0; i < inData.size() - 1 ; i++){
            checksum ^= inData.get(i);
        }

        MyLog.d(TAG,"checksum" + Integer.toHexString(checksum));
        MyLog.d(TAG, "indata" + Integer.toHexString(inData.get(i)));

        if(checksum == inData.get(i))
            return true;
        else
            return false;
    }

    //------------------------------------------------------------------------------
// 形式： byte SerialControl::readData(ArrayList<Byte> content)
// 参数： ArrayList<Byte> content <in>,指令字节串
// 调用者：获取校验码
// 后置：
// 返回值： char checksum，校验码
//------------------------------------------------------------------------------
    public static byte getChecksum(ArrayList<Byte> content) {

        byte checksum = 0x00;

        for(int i = 0; i < content.size() ; i++){
            checksum ^= content.get(i);
        }
        return checksum;
    }

//------------------------------------------------------------------------------
// 形式： byte SerialControl::getChecksum(ArrayList<Byte> content)
// 参数： ArrayList<Byte> content <in>,指令字节串
// 调用者：获取校验码
// 后置：
// 返回值： char checksum，校验码
//------------------------------------------------------------------------------

    //回答查询指令
    public static void queryInsult(FT311UARTInterface sPort){

        byte[] queryInsult = {0x02, 0x00, 0x00, 0x00, 0x0D, (byte) 0x80, 0x00, 0x00,
                0x00, 0x03, 0x00, 0x10, 0x3C, 0x00, 0x00, 0x47, (byte) 0x90, 0x00, 0x75, 0x03};

        sPort.SendData(queryInsult.length, queryInsult);
        MyLog.d(TAG, "we send the queryInsult");
        //回答完查询指令就要置1
        IsQuery = 1;
    }

    //cancel insult, when overtime
    public static void cancelInsult(FT311UARTInterface sPort){
        sPort.SendData(OVERORDER.length, OVERORDER);
        //把IsQuery释放，以后还可以接受
        IsQuery = 0;
    }


    //发送消费指令,return Result 是要发送的字符串
    public static void consumeInsult(List<Byte> returnResult, FT311UARTInterface sPort){
        if(IsQuery == 1) {
            sPort.SendData(arraytobyte.arraytobyte(returnResult).length, arraytobyte.arraytobyte(returnResult));
        }else{
            MyLog.d(TAG, "还没有查询，所以不能发送消费指令");
        }

        //发送完消费指令又可以接收查询指令了
        IsQuery = 0;
    }

    //1、掐头去尾
    //2、得到Length
    //3、从后数length的长度判断是不是扣款指令

    public static void readData(ArrayList<Byte> temp, FT311UARTInterface sPort, Context context) {

        MyLog.d(TAG, "why we can not read!!!!");
        if (temp.isEmpty())
            return;

        //掐头去尾
        if (//judge whether it is the correct message
                (temp.get(0) == (byte) (0x02))
                        && (temp.get(temp.size() - 1) == (byte) (0x03))
                ) {

            MyLog.d(TAG, "掐头去尾 判断成功");

            //去除起始符号，结尾符号,不用转译?
            ArrayList<Byte> data = new ArrayList<Byte>(temp.subList(1, temp.size() - 1));

            MyLog.d(TAG, data.toString());
            //对字符校验位进行校验，掐头去尾的校验，好像要把0x02放进去
            //if(!checkData(data))
            //return;
            MyLog.d(TAG, "校验成功");
            //获取数据长度,不包含校验位
            ArrayList<Byte> dataLength = new ArrayList<>(data.subList(0, 4));

            //type,一般为0x80
            byte type = data.get(4);

            //判断是不是0x80
            if (!(type == (byte) 0x80)) {
                MyLog.d(TAG, "0x80" + type);
                return;
            }

            MyLog.d(TAG, "0x80成功");

            //获取Abdata
            ArrayList<Byte> Abdata = new ArrayList<>(data.subList(14, data.size() - 1));

            if (!((byte) Abdata.get(0) == (byte) 0xbe)) {
                MyLog.d(TAG, "it should be be" + Abdata.get(0));
                return;
            }

            MyLog.d(TAG, "开头是BE开头");

            //取消指令优先级高，先取消
            MyLog.d(TAG, "Abdata.get(1):" + Abdata.get(1));
            if (Abdata.get(1) == (byte) 0x05) {
                MyLog.d(TAG, "we need to cancel it!!");
                //直接发送02 00  00 00 0c 80  00 00 00 02  00 05 3c 00  00 90 00 27  03

                if (hasChoosen) {
                    //已经收到了选货指令。但是这个时候要取消。
                    //这里考虑使用一个广播
                    hasChoosen = false;
                    Bundle bundle = new Bundle();
                    bundle.putString("message", "6");

                    Intent intent = new Intent();
                    MyLog.d(TAG, "send the cancel broadcast");
                    intent.putExtras(bundle);
                    intent.setAction(CANCEL_ACTION);
                    context.sendBroadcast(intent);

                }
                sPort.SendData(CANCELORDER.length, CANCELORDER);
                //发送完取消指令有要可以接收查询指令了
                hasChoosen = false;
                IsQuery = 0;
            }

                switch (Abdata.get(1)) {
                    case (byte) 0x40://获得扣款指令
                        //没有收到查询指令，先收到扣款指令，不处理

                        if (IsQuery == 0) return;
                        else {
                            //找到金额，找到交易序列号，判断原来有没有交易序列号，是否重复
                            //发送扣款成功信号
                            MyLog.d(TAG, "we get the 0x40, 消费指令");
                            hasChoosen = true;

                            //一条线给length赋值
                            byte[] data_length = new byte[4];
                            data_length[0] = Abdata.get(2);
                            data_length[1] = Abdata.get(3);
                            data_length[2] = Abdata.get(4);
                            data_length[3] = Abdata.get(5);

                            int length = AnalyticalImpl.HextoDec(data_length, 4);

                            byte[] passlength = new byte[length];
                            for (int i = 0; i < length; i++) {
                                passlength[i] = Abdata.get(6 + i);
                            }

                            if (!(passlength[0] == (byte) 0x9F && passlength[1] == (byte) 0x02)) {
                                MyLog.d(TAG, "the passlength 0 " + passlength[0]);
                                MyLog.d(TAG, "the passlength 1 " + passlength[1]);
                                return;
                            }
                            MyLog.d(TAG, "下面解析交易金额，9F02,获得了");
                            byte pricelength = passlength[2];
                            int price_length = (int) pricelength;

                            //这个是价格
                            byte[] temp1 = new byte[price_length];
                            for (int i = 0; i < price_length; i++) {
                                temp1[i] = passlength[3 + i];
                            }

                            int price_pay = analyticalImpl.BCDtoDec(temp1, temp1.length);

                            if (!(passlength[3 + price_length] == (byte) 0x9A))
                                return;

                            MyLog.d(TAG, "交易日期得到了");

                            int dateLength = (int) passlength[4 + price_length];

                            if (!(passlength[5 + price_length + dateLength] == (byte) 0x5f &&
                                    passlength[6 + price_length + dateLength] == (byte) 0x21)) {

                                MyLog.d(TAG, "passlength[4 + price_length] + 0x5f:" + passlength[4 + price_length + dateLength]);
                                MyLog.d(TAG, "passlength[5 + price_length + dateLength] + 0x21:" + passlength[5 + price_length + dateLength]);
                                return;
                            }

                            MyLog.d(TAG, "得到交易时间的长度");

                            int timeLength = (int) passlength[7 + price_length + dateLength];

                            if (!(passlength[8 + price_length + dateLength + timeLength] == (byte) 0x9c)) {
                                MyLog.d(TAG, "find 0x9c" + passlength[8 + price_length + dateLength + timeLength]);
                                return;
                            }

                            MyLog.d(TAG, "交易类型确定");

                            int typeLength = (int) passlength[9 + price_length + dateLength + timeLength];

                            if (!(passlength[10 + price_length + dateLength + timeLength + typeLength] == (byte) 0x9F &&
                                    passlength[11 + price_length + dateLength + timeLength + typeLength] == (byte) 0x41)) {
                                MyLog.d(TAG, passlength[10 + price_length + dateLength + timeLength + typeLength]);
                                return;
                            }

                            MyLog.d(TAG, "得到交易序列号");
                            int seqLength = (int) passlength[12 + price_length + dateLength + timeLength + typeLength];

                            MyLog.d(TAG, seqLength);
                            byte[] currentSeq = new byte[seqLength];

                            for (int i = 0; i < seqLength; i++) {
                                currentSeq[i] = passlength[13 + price_length + dateLength + timeLength + typeLength + i];
                            }

                            int flag = 0;

                            MyLog.d(TAG, "currentSeq 0" + currentSeq[0]);
                            MyLog.d(TAG, "currentSeq seqLength - 1" + currentSeq[seqLength - 1]);

                            if (!(PreSeq == null) && currentSeq.length == PreSeq.size()) {

                                for (int i = 0; i < currentSeq.length; i++) {
                                    if (currentSeq[i] != PreSeq.get(i))
                                        flag++;
                                }
                                MyLog.d(TAG, "before flag+" + flag);
                                if (flag > 0)
                                    flag = 0;
                                MyLog.d(TAG, "after flag" + flag);
                            }

                            MyLog.d(TAG, "flag +" + flag);

                            if (flag == 0) {

                                MyLog.d(TAG, "This is broadcast");

                                //得到一个目前的SaleSeq，
                                PreSeq = new ArrayList<>(arraytobyte.changetoarraylist(currentSeq));
                                //发送广播
                                Bundle bundle = new Bundle();
                                bundle.putInt("price", price_pay);
                                bundle.putByteArray("curSeq", currentSeq);

                                Intent intent = new Intent();
                                MyLog.d(TAG, "send the broadcast");
                                intent.putExtras(bundle);
                                intent.setAction(BROADCAST_ACTION);
                                context.sendBroadcast(intent);

                            } else return;
                        }

                        break;
                    case (byte) 0x10://获得查询指令
                        MyLog.d(TAG, "we get the query insult");
                        //if (IsQuery == 1)return;
                        //else {
                        //获得查询之后就立马发送，查询的应答
                        queryInsult(sPort);
                        IsQuery = 1;
                        MyLog.d(TAG, "IsQuery has turned 1");
                        //}
                        break;
                    default:
                        return;
                }
            }
        }
}
