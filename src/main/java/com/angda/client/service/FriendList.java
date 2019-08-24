package com.angda.client.service;

import com.angda.util.CommUtil;
import com.angda.vo.MessageVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FriendList {
    private JPanel frindListPanel;
    private JScrollPane friendPanel;
    private JButton createGroupBtn;
    private JScrollPane groupPanel;
    private String myname;
    private Connect2Server connect2Server;
    private Set<String> names;

    //缓存所有的私聊界面
    private Map<String, PrivateChatGUI> privateChatGUIMap=new ConcurrentHashMap<>();
    //缓存当前客户端群聊信息
    private Map<String,Set<String>> group=new ConcurrentHashMap<>();
    //缓存当前客户端群聊界面
    private Map<String,GroupchatGUI> groupchatGUIMap=new ConcurrentHashMap<>();

    private class DaemonTask implements Runnable{
        private Scanner scanner=new Scanner(connect2Server.getIn());
        @Override
        public void run() {
            while (true){
                if(scanner.hasNextLine()){
                    String strFromServer=scanner.nextLine();
                    if(strFromServer.startsWith("newLogin:")){
                        //好友上线提醒
                        String newFriend=strFromServer.split(":")[1];
                        JOptionPane.showMessageDialog(null,
                                newFriend+"上线了！","上线提醒",
                                JOptionPane.INFORMATION_MESSAGE);
                        names.add(newFriend);
                        //刷新好友列表
                        reloadFriendList();
                    }
                    if(strFromServer.startsWith("{")){
                        //此时是个json串
                        MessageVO messageVOFromClient= (MessageVO) CommUtil.json2Object
                                (strFromServer,MessageVO.class);
                        if(messageVOFromClient.getType().equals(2)){
                            String senderName=messageVOFromClient.getContent().split("-")[0];
                            String msg=messageVOFromClient.getContent().split("-")[1];
                            if(privateChatGUIMap.containsKey(senderName)){
                                PrivateChatGUI privateChatGUI=privateChatGUIMap.get(senderName);
                                privateChatGUI.getFrame().setVisible(true);
                                privateChatGUI.readFromServer(senderName+"说："+msg);
                            }else {
                                PrivateChatGUI privateChatGUI=new PrivateChatGUI
                                        (senderName,myname,connect2Server);
                                privateChatGUIMap.put(senderName,privateChatGUI);
                                privateChatGUI.readFromServer(senderName+"说："+msg);
                            }
                        }
                        else if(messageVOFromClient.getType().equals(4)){
                            //群聊信息
                            String sendName=messageVOFromClient.getContent().split("-")[0];
                            String groupMsg=messageVOFromClient.getContent().split("-")[1];
                            String groupName=messageVOFromClient.getTo().split("-")[0];
                            if(group.containsKey(groupName)){
                                if(groupchatGUIMap.containsKey(groupName)){
                                    GroupchatGUI groupchatGUI=groupchatGUIMap.get(groupName);
                                    groupchatGUI.getFrame().setVisible(true);
                                    groupchatGUI.readFrommServer(sendName+"说"+groupMsg);
                                }else {
                                    Set<String> friends=group.get(groupName);
                                    GroupchatGUI groupchatGUI=new GroupchatGUI(groupName,friends,
                                            connect2Server,myname);
                                    groupchatGUIMap.put(groupName,groupchatGUI);
                                    groupchatGUI.readFrommServer(sendName+"说"+groupMsg);
                                }
                            }else {
                                //将群信息以及群成员添加到本客户端群聊列表
                                Set<String> friends= (Set<String>) CommUtil.json2Object(messageVOFromClient.getTo(),
                                        Set.class);
                                addGroupInfo(groupName,friends);
                                reloadFriendList();
                                //2.换起群聊界面
                                GroupchatGUI groupchatGUI=new GroupchatGUI(groupName,friends,connect2Server,myname);
                                groupchatGUIMap.put(groupName,groupchatGUI);
                                groupchatGUI.readFrommServer(sendName+"说"+groupMsg);
                            }
                        }

                    }
                }
            }
        }
    }
    private class PrivateLabelAction implements MouseListener{
        private String labelName;
        public PrivateLabelAction(String labelName){
            this.labelName=labelName;
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            //判断缓存中有没有私聊界面
            if(privateChatGUIMap.containsKey(labelName)){
                PrivateChatGUI privateChatGUI=privateChatGUIMap.get(labelName);
                privateChatGUI.getFrame().setVisible(true);
            }else {
                PrivateChatGUI privateChatGUI=new PrivateChatGUI(labelName,myname,connect2Server);
                privateChatGUIMap.put(labelName,privateChatGUI);
            }

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    private class GroupLabelAction implements MouseListener{
        private String groupName;

        public GroupLabelAction(String groupName) {
            this.groupName = groupName;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(groupchatGUIMap.containsKey(groupName)){
                GroupchatGUI groupchatGUI=groupchatGUIMap.get(groupName);
                groupchatGUI.getFrame().setVisible(true);
            }else {
                Set<String> friends=group.get(groupName);
                GroupchatGUI groupchatGUI=new GroupchatGUI(groupName,friends,connect2Server,myname);
                groupchatGUIMap.put(groupName,groupchatGUI);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    public FriendList(String myname,
                      Connect2Server connect2Server,
                      Set<String> names) {
        this.myname=myname;
        this.connect2Server=connect2Server;
        this.names=names;
        JFrame frame = new JFrame("FriendList");
        frame.setContentPane(frindListPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        reloadFriendList();

        //新启动一个后台线程不断监听服务器发送信息
        Thread daemonThread=new Thread(new DaemonTask());
        daemonThread.setDaemon(true);
        daemonThread.start();
        //点击创建群组弹出界面
        createGroupBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateGroupGUI(names,myname,
                        connect2Server,FriendList.this);
            }
        });
    }

    public void reloadFriendList(){
        JPanel friendLabelPanel=new JPanel();
        JLabel[] labels=new JLabel[names.size()];
        //迭代遍历set集合
        Iterator<String> iterator=names.iterator();
        friendLabelPanel.setLayout(new BoxLayout(friendLabelPanel,
                BoxLayout.Y_AXIS));
        int i=0;
        while(iterator.hasNext()){
            String labelname=iterator.next();
            labels[i]=new JLabel(labelname);
            labels[i].addMouseListener(new PrivateLabelAction(labelname));
            friendLabelPanel.add(labels[i]);
            i++;
        }
        this.friendPanel.setViewportView(friendLabelPanel);
        //设置滚动条为垂直滚动条
        this.friendPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.friendPanel.revalidate();
    }

    //刷新新群聊列表的信息
    public void reloadGroupList(){
        JPanel jPanel=new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.Y_AXIS));
        Set<String> groupNames=group.keySet();
        Iterator<String> iterator=groupNames.iterator();
        while (iterator.hasNext()){
            String groupName=iterator.next();
            JLabel label=new JLabel(groupName);
            label.addMouseListener(new GroupLabelAction(groupName));
            jPanel.add(label);
        }
        groupPanel.setViewportView(jPanel);
        groupPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        groupPanel.revalidate();

    }
    public void addGroupInfo(String groupName,Set<String> friends){
        group.put(groupName,friends);
    }
}
