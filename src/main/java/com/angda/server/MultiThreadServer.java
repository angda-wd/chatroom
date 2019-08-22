package com.angda.server;

import com.angda.util.CommUtil;
import com.angda.vo.MessageVO;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {
    private  static final Integer PORT;
    static {
        Properties pros= CommUtil.loadProperties("socket.properties");
        PORT= Integer.valueOf(pros.getProperty("PORT"));
    }
    //服务器缓存所有连接的客户端对象
    private static Map<String, Socket> clients=new ConcurrentHashMap<>();

    private static class  ExecuteClient implements Runnable{
        private Socket client;
        private Scanner in;
        private PrintStream out;

        public ExecuteClient(Socket client) {
            this.client = client;
            try {
                this.in=new Scanner(client.getInputStream());
                this.out=new PrintStream(client.getOutputStream(),
                        true,"UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            while(true){
                if(in.hasNextLine()){
                    String strFromClient=in.nextLine();
                    MessageVO msgFromClient= (MessageVO) CommUtil.json2Object(strFromClient,MessageVO.class);
                    if(msgFromClient.getType().equals(1)){
                        //新用户注册
                        String userName=msgFromClient.getContent();
                        //1.将当前聊天室在线好友信息发回给新用户
                        Set<String> names=clients.keySet();
                        MessageVO msg2Client=new MessageVO();
                        msg2Client.setType(1);
                        msg2Client.setContent(CommUtil.object2Json(names));
                        out.println(CommUtil.object2Json(msg2Client));
                        //将新用户上线信息发给其他在线用户
                        String loginMsg="newLogin:"+userName;
                        for(Socket socket:clients.values()){
                            try {
                                PrintStream out=new PrintStream(socket.getOutputStream(),
                                        true,"UTF-8");
                                out.println(loginMsg);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //将新用户信息保存到当前服务器缓存
                        System.out.println(userName+"上线了");
                        clients.put(userName,client);
                        System.out.println("当前聊天室在线人数为:"+clients.size());
                    }
                }
            }
        }
    }
    public static void main(String[] args)throws IOException {
        ServerSocket server=new ServerSocket(PORT);
        ExecutorService executors= Executors.newFixedThreadPool(50);
        for(int i=0;i<50;i++){
            Socket client=server.accept();
            executors.submit(new ExecuteClient(client));
        }

    }
}
