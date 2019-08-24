package com.angda.client.service;

import com.angda.util.CommUtil;
import com.angda.vo.MessageVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class PrivateChatGUI {
    private JPanel privateChatPanel;
    private JTextArea readFromServer;
    private JTextField send2Server;
    private JFrame frame;

    private String friendName;
    private String myName;
    private Connect2Server connect2Server;


    public PrivateChatGUI(String friendName,
                          String myName,
                          Connect2Server connect2Server) {
        this.friendName=friendName;
        this.myName=myName;
        this.connect2Server=connect2Server;
        frame = new JFrame("与"+friendName+"私聊中");
        frame.setContentPane(privateChatPanel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400,400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        //添加一个输入框的事件

        send2Server.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                StringBuilder sb=new StringBuilder();
                sb.append(send2Server.getText());
                //当按下回车键 发送信息
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    String str=sb.toString();
                    MessageVO messageVO=new MessageVO();
                    messageVO.setType(2);
                    messageVO.setContent(myName+"-"+str);
                    messageVO.setTo(friendName);
                    try {
                        PrintStream out=new PrintStream(connect2Server.getOut(),
                                true,"UTF-8");
                        out.println(CommUtil.object2Json(messageVO));
                        //刷新自己的信息框
                        readFromServer.append(myName+"说"+str+"\n");
                        //清空
                        send2Server.setText("");
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    public void readFromServer(String msg){
        readFromServer.append(msg+"\n");
    }

    public JFrame getFrame() {
        return frame;
    }
}
