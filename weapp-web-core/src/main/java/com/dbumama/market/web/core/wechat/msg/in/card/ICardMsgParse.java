package com.dbumama.market.web.core.wechat.msg.in.card;


import com.dbumama.market.encrypt.XmlHelper;

/**
 * 卡券消息解析接口
 * @author L.cm
 */
public interface ICardMsgParse {
    /**
     * 分而治之
     * @param xmlHelper xml解析工具
     */
    void parse(XmlHelper xmlHelper);
}
