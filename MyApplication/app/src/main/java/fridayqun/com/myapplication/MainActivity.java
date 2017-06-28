package fridayqun.com.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import fridayqun.com.myapplication.impl.arraytobyte;
import fridayqun.com.myapplication.logutil.MyLog;
import fridayqun.com.myapplication.serviceutil.ComService;

import java.util.ArrayList;

public class MainActivity extends Activity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private static Handler sHandler;
    //反射机制
    private static String TAG = MainActivity.class.getSimpleName();
    private static final String BROADCAST_ACTION = "Price_curSeq";
    private static int price;
    private static ArrayList<Byte> curSeq;
    private Handler mHandler = null;
    private static Intent intentService;
    private BroadcastReceiver detachReceiver;
    private BroadcastReceiver attachReceiver;
    public static Context FULL_CONTEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.INVISIBLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
        FULL_CONTEXT = MainActivity.this;
        toggleHideyBar();
        setContentView(R.layout.activity_main);

        sHandler = new Handler();
        //拦截所有的touch event
        decorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        MyLog.d(TAG, "Activity on Create.");
        //启动串口Service
        intentService = new Intent(MainActivity.this, ComService.class);
        startService(intentService);

        detachReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(UsbManager.ACTION_USB_ACCESSORY_DETACHED))
                    MyLog.d("bad", "bad");
                Toast toast = Toast.makeText(context, "Usb Detached", Toast.LENGTH_SHORT);
                toast.show();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        registerReceiver(detachReceiver, filter);


        attachReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(UsbManager.ACTION_USB_ACCESSORY_ATTACHED))
                    MyLog.d("ok", "ok");
                Toast toast = Toast.makeText(context, "Usb Attached", Toast.LENGTH_SHORT);
                toast.show();
                startService(intentService);
            }
        };

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        registerReceiver(attachReceiver, filter1);


        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener(){

                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        sHandler.post(mHideRunnable);
                    }
                });
    }

    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            int flags;
            int curApiVersion = android.os.Build.VERSION.SDK_INT;
            // This work only for android 4.4+
            if(curApiVersion >= Build.VERSION_CODES.KITKAT){
                // This work only for android 4.4+
                // hide navigation bar permanently in android activity
                // touch the screen, the navigation bar will not show
                flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;

            }else{
                // touch the screen, the navigation bar will show
                flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            // must be executed in main thread :)
            getWindow().getDecorView().setSystemUiVisibility(flags);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        toggleHideyBar();

        //启动串口Service
        startService(intentService);
        mHandler = new Handler();

        //carouselerView = (CarouselerView)findViewById(R.id.circleView);
        //ArrayList bitmaps = new ArrayList();
        //bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.b1));
        //carouselerView.setImageResources(bitmaps);
        //carouselerView.setAutoSlide(true);

        //首先初始化,wechat init.
        WXPay.initSDKConfiguration("hd749fjh01kgyapq59hdogp02fdmvbxn", "wx67af0e3f8faaca3e", "10057360");

        BroadcastReceiver mbroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                price = bundle.getInt("price");
                byte[] curSeqArray = bundle.getByteArray("curSeq");
                curSeq = arraytobyte.changetoarraylist(curSeqArray);
                MyLog.d(TAG, "when onReceive, the price is :" + price + "the curSeq is :" + curSeq.toString());

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        WindowUtils windowUtils = new WindowUtils();
                        windowUtils.showPopupWindow(MainActivity.this, price, curSeq);
                    }
                });
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        registerReceiver(mbroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        toggleHideyBar();
        //启动串口Service
        startService(intentService);
    }

    @Override
    protected void onPause() {
        super.onPause();
        toggleHideyBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void toggleHideyBar() {

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch (action){
            case (MotionEvent.ACTION_DOWN) :
                MyLog.d(TAG,"Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE) :
                MyLog.d(TAG,"Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP) :
                MyLog.d(TAG,"Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                MyLog.d(TAG,"Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                MyLog.d(TAG,"Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default :
                return true;
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}

