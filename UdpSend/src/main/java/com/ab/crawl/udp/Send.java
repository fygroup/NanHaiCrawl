package com.ab.crawl.udp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.selector.Json;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/08 11:21
 * @see com.ab.crawl.udp
 */
public class Send {

    public void UdpSend(Json p) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        String s = JSONObject.toJSONString( p );
        byte[] buf = s.getBytes();
        //将数据打包
        DatagramPacket packet = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName("192.168.104.110"), 8811);
        socket.send(packet);
        socket.close();

    }

    public void UdpSend(JSONObject p) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        String s = JSONObject.toJSONString( p );
        byte[] buf = s.getBytes();
        //将数据打包
        DatagramPacket packet = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName("192.168.104.110"), 8812);
        socket.send(packet);
        socket.close();

    }

    public void UdpSend(String p) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        String s = JSONObject.toJSONString( p );
        byte[] buf = s.getBytes();
        //将数据打包
        DatagramPacket packet = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName("192.168.104.110"), 8813);
        socket.send(packet);
        socket.close();

    }
}
