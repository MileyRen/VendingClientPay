package fridayqun.com.myapplication.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import fridayqun.com.myapplication.MainActivity;
import fridayqun.com.myapplication.serviceutil.ComService;

/**
 * Created by lenovo on 2016/5/27.
 */
public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent startIntent = new Intent(context, MainActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //开机启动Service
        Intent intentService = new Intent(context, ComService.class);
        context.startService(intentService);

        context.startActivity(startIntent);

    }
}
