package com.ab.crawl.udp;

import com.ab.crawl.util.PropertiesConfig;
import com.ab.crawl.util.SetSystemProperty;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * <Description>
 * udp传输，支持中文
 * @author Xiapazi
 * @version 1.0
 * @createDate 2019/01/08 11:21
 * @see com.ab.crawl.udp
 */
public class Send {
    /**
     * 传输的端口号
     */
    //private static final String configIp = PropertiesConfig.INSTANCE.getProPerties("udpip");
    private static final String configIp = SetSystemProperty.getKeyValue("udpip");
    /**
     * 采用DatagramSocket+DatagramPacket的模式，使用IO流的写入和读取
     * @param p 需要传输的字符串
     * @param port 传输的端口号
     * @throws IOException
     */
    public boolean UdpSend(String p,int port)  {
        try {
            DatagramSocket socket = new DatagramSocket();
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            DataOutputStream dataStream = new DataOutputStream(ostream);
            dataStream.writeUTF(p);
            dataStream.close();
            byte[] data = ostream.toByteArray();
            //将数据打包
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(configIp), port );
            socket.send(packet);
            socket.close();
            return true;
        }catch (Exception e){
            return false;
        }

    }
}
