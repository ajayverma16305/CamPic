package com.androidteam.campic.MainModule.cache;

import android.content.Context;
import android.os.Environment;

import com.androidteam.campic.MainModule.AppApplication.CamPicApplication;
import com.imnjh.imagepicker.util.FileUtil;
import java.io.File;

public class InnerCache extends Cache {

  private File innerCache;

  public InnerCache() {
    innerCache = getCacheDirCreateIfNotExist();
  }

  private File getCacheDirCreateIfNotExist() {
    File cachePath = new File(getInnerCacheDir(CamPicApplication.getAppContext()));
    if (!cachePath.isDirectory()) {
      try {
        cachePath.mkdirs();
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        new File(cachePath, ".nomedia").createNewFile();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return cachePath;
  }

  public String getInnerCacheDir(Context context) {
    String cachePath;
    cachePath = Environment.getExternalStorageDirectory() + File.separator +
            Environment.DIRECTORY_DCIM + File.separator + CacheManager.ROOT_STORE + File.separator + "CamPicEdited" + File.separator;
    return cachePath;
  }


  public boolean exist(String fileName) {
    String path = innerCache + File.separator + fileName;
    return FileUtil.exist(path);
  }

  @Override
  public String getAbsolutePath(String fileName) {
    return getDirectory().getAbsolutePath() + File.separator /*+ ((System.currentTimeMillis()) + fileName)*/;
  }

  @Override
  public File getDirectory() {
    return getCacheDirCreateIfNotExist();
  }

  @Override
  public boolean deleteCacheItem(String fileName) {
    String filePath = getAbsolutePath(fileName);
    return FileUtil.deleteFile(filePath);
  }
}
