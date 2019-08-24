package com.angda.client.service;

import com.angda.util.CommUtil;
import com.angda.vo.MessageVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CreateGroupGUI {
    private JPanel createGroupPanel;
    private JPanel checkBoxPanel;
    private JButton confromBtn;
    private JTextField groupNameText;
    private Set<String> friends;
    private String myName;
    private Connect2Server connect2Server;
    private FriendList friendList;

    public CreateGroupGUI(Set<String> friends,
                          String myName,
                          Connect2Server connect2Server,
                          FriendList friendList) {
        this.friends=friends;
        this.myName=myName;
        this.connect2Server=connect2Server;
        this.friendList=friendList;
        JFrame frame = new JFrame("创建群组");
        frame.setContentPane(createGroupPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //1.动态的添加checkBox
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel,
                BoxLayout.Y_AXIS));
        Iterator<String> iterator=friends.iterator();
        while(iterator.hasNext()){
            String friendName=iterator.next();
            JCheckBox checkBox=new JCheckBox(friendName);
            checkBoxPanel.add(checkBox);
        }
        checkBoxPanel.revalidate();
        //2.提交信息按键
        confromBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //1.获取群名称
                String groupName=groupNameText.getName();
                //2.获取选中的名称
                Set<String> selectFriends=new HashSet<>();
                //3.获取checkBoxPanel下的所有组件
                Component[] components=checkBoxPanel.getComponents();
                for (Component component :components) {
                    JCheckBox checkBox= (JCheckBox) component;
                    if(checkBox.isSelected()){
                        selectFriends.add(checkBox.getText());
                    }
                }
                selectFriends.add(myName);
                //4.将群名称与选择的好友发送到服务端
                MessageVO messageVO=new MessageVO();
                messageVO.setType(3);
                messageVO.setContent(groupName);
                messageVO.setTo(CommUtil.object2Json(selectFriends));
                try {
                    PrintStream out=new PrintStream(connect2Server.getOut(),
                            true,"UTF-8");
                    out.println(CommUtil.object2Json(messageVO));
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                frame.setVisible(false);
                //返回好友列表
                friendList.addGroupInfo(groupName,selectFriends);
                friendList.reloadGroupList();

            }
        });
    }
}
