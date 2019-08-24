package com.angda.vo;

import lombok.Data;

/**
 * 服务端和客户端通信载体
 */
@Data
public class MessageVO {
    /**
     * 告知服务端要进行的操作 eg:1.注册 2.私聊等
     */
    private Integer type;
    //聊天内容
    private String content;
    //发送对象
    private String to;

}
