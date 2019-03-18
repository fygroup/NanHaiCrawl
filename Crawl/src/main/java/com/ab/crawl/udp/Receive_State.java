package com.ab.crawl.udp;


import com.alibaba.fastjson.JSONObject;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/09 19:26
 */
public class Receive_State extends Thread{
    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(8816);//如果有指定的接收数据的本地端口，则填入本地端口号；没有则不用
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf,buf.length);
            while (true){
                socket.receive(packet);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength());
                DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                String msg= dataInputStream.readUTF();
                //receiveStr = new String(buf,0,packet.getLength());//获取接收的数据
                JSONObject jsonObject = JSONObject.parseObject(msg);
                System.out.println(jsonObject.getString("kIND"));
                State state= JSONObject.parseObject(msg, State.class);
                System.out.println(state);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
