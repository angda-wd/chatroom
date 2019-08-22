package com.angda.client.service;

import com.angda.client.dao.AccountDao;
import com.angda.client.entity.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserReg {
    private JPanel regPanel;
    private JTextField userNameText;
    private JPasswordField passwordText;
    private JButton confimBtn;
    private JLabel brieftext;
    private AccountDao accountDao=new AccountDao();

    public UserReg() {
        JFrame frame = new JFrame("用户注册");
        frame.setContentPane(regPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        //点击提交按钮触发此方法
        confimBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              //1.获取信息
              String username=userNameText.getText();
              String password=String.valueOf(passwordText.getPassword());
              String breif=brieftext.getText();
              //2.调用dao方法将信息持久化到数据库
                User user=new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setBreif(breif);
                if(accountDao.userReg(user)){
                    JOptionPane.showMessageDialog(null,"注册成功",
                            "成功信息",JOptionPane.INFORMATION_MESSAGE);
                    frame.setVisible(false);
                }else {
                    JOptionPane.showMessageDialog(null,"注册失败",
                            "失败信息",JOptionPane.ERROR_MESSAGE);

                }
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
