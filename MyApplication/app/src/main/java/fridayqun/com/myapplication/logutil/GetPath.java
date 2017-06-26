package fridayqun.com.myapplication.logutil;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by lenovo on 2016/4/17.
 */
public class GetPath {

    public static String getPath(Context context){

        String XML_Local = null;

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            XML_Local = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + "XuRong";

        } else {// 如果SD卡不存在，就保存到本应用的目录下
            XML_Local = context.getFilesDir().getAbsolutePath()
                    + File.separator + "XuRong";

        }
        MyLog.w("TAG","saved path = "+ XML_Local);

        return XML_Local;

    }
}
