package com.angda.client.service;

import com.angda.client.dao.AccountDao;
import com.angda.client.entity.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
                    JOptionPane.showMessageDialog(null,
                            "登陆成功","提示信息",JOptionPane.INFORMATION_MESSAGE);
                }else {
                    JOptionPane.showMessageDialog(null,
                            "登陆失败","失败信息",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("用户登录");
        frame.setContentPane(new userlogin().loginPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
