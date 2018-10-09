package com.mpen.bluetooth.utils;

import com.mpen.bluetooth.common.BtDataResult;
import com.mpen.bluetooth.common.OperationType;

/**
 * Created by LYH on 2017/9/28.
 * 工具类.
 */

public class ResultUtils {

    /**
     * 请求类型的数据封装
     *
     * @param resultData
     * @param type
     * @return
     */

    public static BtDataResult resultToStandardData(Object resultData, OperationType type) {
        if (resultData instanceof BtDataResult) {//如果是 BtDataResult 类型直接 转换成json
            return (BtDataResult) resultData;
        }
        BtDataResult ResultData = new BtDataResult();
        ResultData.type = type.getType();
        ResultData.data = resultData;
        return ResultData;
    }

    /**
     * 请求类型的数据封装
     *
     * @param resultData
     * @param type
     * @return
     */

    public static BtDataResult resultToStandardData(Object resultData, int type) {
        if (resultData instanceof BtDataResult) {//如果是 BtDataResult 类型直接 转换成json
            return (BtDataResult) resultData;
        }
        BtDataResult ResultData = new BtDataResult();
        ResultData.type = type;
        ResultData.data = resultData;
        return ResultData;
    }


}
