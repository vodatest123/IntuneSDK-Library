package com.vodafone.intune_lib.logging;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class Utility {

    public static final String SDCARD_LOG_FOLDER_NAME = "IntunePocLogData";

    public static String getLogSDPath(Context context)
    {
        //appending /Android/data so that the data will be deleted on uninstall automatically
        String SDFolder = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Android/data/" +
                context.getPackageName() + "/files" + File.separator + SDCARD_LOG_FOLDER_NAME;

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            new File(SDFolder).mkdirs(); // Create the folder
            return SDFolder;
        } else  {
            return context.getFilesDir().getAbsolutePath();
        }
    }

}
