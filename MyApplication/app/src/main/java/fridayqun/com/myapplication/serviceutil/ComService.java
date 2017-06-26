package fridayqun.com.myapplication.serviceutil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fridayqun.com.myapplication.impl.arraytobyte;
import fridayqun.com.myapplication.logutil.LogcatHelper;
import fridayqun.com.myapplication.serialImp.SerialControl;
import fridayqun.com.myapplication.util.FT311UARTInterface;

/**
 * Created by lenovo on 2016/7/14.
 */
public class ComService extends Service {

    private final String TAG = ComService.class.getSimpleName();

    byte[] readBuffer;
    int[] actualNumBytes;
    byte status;

    private final int baudRate = 115200; /*baud rate*/
    private final byte stopBit = 0x01; /*1:1stop bits, 2:2 stop bits*/
    private final byte dataBit = 0x08; /*8:8bit, 7: 7bit*/
    private final byte parity = 0x00;  /* 0: none, 1: odd, 2: even, 3: mark, 4: space*/
    private final byte flowControl = 0x00; /*0:none, 1: flow control(CTS,RTS)*/

    public static FT311UARTInterface getsPort() {
        return uartInterface;
    }

    private static FT311UARTInterface uartInterface;
    private final static ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogcatHelper.getInstance(this).start();
        /*declare a FT311 UART interface variable*/
        uartInterface = new FT311UARTInterface(ComService.this, null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        uartInterface.ResumeAccessory();
        uartInterface.SetConfig(115200, (byte) 0x08, (byte) 0x01, (byte) 0x00, (byte)0x00);
        handler_thread readDataThread = new handler_thread();
        mExecutor.submit(readDataThread);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uartInterface.DestroyAccessory(true);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private class handler_thread extends Thread {

        public void run() {

            while (true) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }
                readBuffer = new byte[250];
                actualNumBytes = new int[1];

                status = uartInterface.ReadData(250, readBuffer,actualNumBytes);
                byte[] data = new byte[actualNumBytes[0]];
                for(int i = 0; i < actualNumBytes[0]; i++){
                    //MyLog.d(TAG, readBuffer[i]);
                    data[i] = readBuffer[i];
                    //MyLog.d(TAG, "data[" + i + "] = " + data[i]);
                }
                ArrayList<Byte> tt = arraytobyte.changetoarraylist(data);
                //MyLog.d(TAG, "tt:" + tt.toString());
                if(status == 0x00 && actualNumBytes[0] > 0) {
                    SerialControl.readData(tt, uartInterface, ComService.this);
                }
            }
        }
    }
}
