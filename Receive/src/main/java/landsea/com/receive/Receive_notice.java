package landsea.com.receive;

import com.alibaba.fastjson.JSONObject;
import landsea.com.domain.Base;
import landsea.com.save.NoticeSave;
import landsea.com.save.ToTyphoon;
import landsea.com.save.TyphoonSave;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/25 10:57
 * @see landsea.com.receive
 */
public class Receive_notice extends Thread{
    @Override
    public  void run()  {
        String receiveStr = null;
        try {
            DatagramSocket socket = new DatagramSocket(8813);
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf,buf.length);
            while (true){
                socket.receive(packet);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength());
                DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
                String msg= dataInputStream.readUTF();
                //receiveStr = new String(buf,0,packet.getLength());//获取接收的数据
                new NoticeSave().save(msg);
                System.out.println(msg);
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
