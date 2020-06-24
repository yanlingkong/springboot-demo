package com.example.demo.common.constant;

/**
 * @Auther: liuc
 * @Date: 18-12-16 16:29
 * @email i@liuchaoboy.com
 * @Description: 系统提示静态变量
 */
public class MsgConstant {
    /**
     * 操作成功
     */
    public static final String MSG_OPERATION_SUCCESS = "操作成功！";

    /**
     * 操作失败
     */
    public static final String MSG_OPERATION_FAILED = "操作失败！";

    /**
     * 加载表单数据错误提示
     */
    public static final String MSG_INIT_FORM = "初始化表单数据失败，请重试！";

    public static final String DATA_ROWS = "rows";

    /**
     * 删除数据项不是全部所选
     * @param total
     * @param process
     * @return
     */
    public static String removeFailed(int total, int process){
        return "本次共处理："+String.valueOf(total)+"条，成功："+String.valueOf(process)+"条！";
    }
}
