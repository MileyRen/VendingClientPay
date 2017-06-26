package fridayqun.com.myapplication.logutil;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by lenovo on 2016/3/18.
 */
public class LogcatHelper {

    private static LogcatHelper INSTANCE = null;
    private static String PATH_LOGCAT;
    private LogDumper mLogDumper = null;
    private String mPId;

    /**
     * 初始化目录
     */
    public void init(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            PATH_LOGCAT = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + "XuRong";

        } else {// 如果SD卡不存在，就保存到本应用的目录下

            PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
                    + File.separator + "XuRong";

        }
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static LogcatHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper(context);
        }
        return INSTANCE;
    }

    private LogcatHelper(Context context) {
        init(context);
        int pid = android.os.Process.myPid();
        String mPId = String.format("%04d",pid);
    }

    public void start() {
        if (mLogDumper == null)
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
        mLogDumper.start();
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }
}