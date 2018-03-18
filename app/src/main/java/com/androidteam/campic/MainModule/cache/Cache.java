package com.androidteam.campic.MainModule.cache;

import com.imnjh.imagepicker.util.FileUtil;
import java.io.File;

public abstract class Cache {

  public abstract boolean exist(String fileName);

  public abstract String getAbsolutePath(String fileName);

  public abstract File getDirectory();

  public abstract boolean deleteCacheItem(String fileName);

  public void clear() {
    FileUtil.deleteDirectory(getDirectory());
  }
}
