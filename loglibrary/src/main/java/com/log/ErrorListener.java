package com.log;

import java.io.File;

public interface ErrorListener {
    /**
     * description：发生错误的时候回调
     *
     * @param errorMsg 错误日志
     * @param anr      anr日志
     * @param logFiles 日志文件地址
     */
    void onError(String errorMsg, String anr, File[] logFiles);
}
