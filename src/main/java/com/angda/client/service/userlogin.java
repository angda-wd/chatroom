package com.angda.client.service;

import com.angda.client.dao.AccountDao;
import com.angda.client.entity.User;
import com.angda.util.CommUtil;
import com.angda.vo.MessageVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Set;

public class userlogin {
    private JPanel loginPanel;
    private JPanel userNamePanel;
    private JTextField usernameText;
    private JPanel btnPanel;
    private JButton regBtn;
    private JButton loginBtn;
    private JPasswordField passwordText;

    private AccountDao accountDao=new AccountDao();
    public userlogin() {
        JFrame frame = new JFrame("用户登录");
        frame.setContentPane(loginPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(300,300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //点击注册按钮 跳转注册
        regBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               new UserReg();
            }
        });
        //登录
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username=usernameText.getText();
                String password=String.valueOf(passwordText.getPassword());
                User user=accountDao.userLogin(username,password);
                if(user!=null){
                    //登陆成功
                    JOptionPane.showMessageDialog(null,
                            "登陆成功","提示信息",JOptionPane.INFORMATION_MESSAGE);
                    //与服务器建立连接，将用户名注册到服务器缓存
                    Connect2Server connect2Server=new Connect2Server();
                    MessageVO messageVO=new MessageVO();
                    messageVO.setType(1);
                    messageVO.setContent(username);
                    String msgJson= CommUtil.object2Json(messageVO);
                    try {
                        //发送信息
                        PrintStream out=new PrintStream(connect2Server.getOut(),
                                true,"UTF-8");
                        out.println(msgJson);
                        //读取服务端发回的响应，加载用户列表
                        Scanner in=new Scanner(connect2Server.getIn());
                        if(in.hasNextLine()){
                            String jsonStr=in.nextLine();
                            MessageVO msgFromServer= (MessageVO) CommUtil.json2Object(jsonStr,MessageVO.class);
                            Set<String> names= (Set<String>) CommUtil.json2Object(msgFromServer.getContent(),Set.class);
                            System.out.println("在线好友为:"+names);
                            //加载好友列表，登录页面不可见
                            frame.setVisible(false);
                            //跳转到好友列表需要传递用户名和与服务器建立的连接
                            new FriendList(username,connect2Server,names);
                        }
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }

                }else {
                    JOptionPane.showMessageDialog(null,
                            "登陆失败","失败信息",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
      new userlogin();
    }
}
