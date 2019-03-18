package landsea.com.receive;


import com.alibaba.fastjson.JSONObject;
import landsea.com.save.StateSave;
import landsea.com.save.WeatherSave;

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
 * @see landsea.com.receive
 */
public class Receive_weather extends Thread{
    @Override
    public void run() {
        String receiveStr = null;
        try {
            DatagramSocket socket = new DatagramSocket(8812);//如果有指定的接收数据的本地端口，则填入本地端口号；没有则不用
            byte[] buf = new byte[16000];
            DatagramPacket packet = new DatagramPacket(buf,buf.length);
            while (true){
                socket.receive(packet);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength());
                DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

                String json_receive = dataInputStream.readUTF();
                json_receive.replace("@","");
                JSONObject jsonObject = JSONObject.parseObject(json_receive);
                new WeatherSave().save(jsonObject);
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
