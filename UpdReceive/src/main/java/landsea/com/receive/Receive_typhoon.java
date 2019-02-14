package landsea.com.receive;


import landsea.com.domain.Base;
import landsea.com.save.ToTyphoon;;
import landsea.com.save.TyphoonSave;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/09 19:26
 * @see landsea.com.receive
 */
public class Receive_typhoon {
    //TODO 如果台风停止了 ，怎么改变数据库中 states的值！！！！！！！！！
    public static void main(String[] args)throws IOException{
        String str_send = "Hello UDPclient";
        byte[] buf = new byte[160000];
        //服务端在8811端口监听接收到的数据
        DatagramSocket ds = new DatagramSocket(8811);
        //接收从客户端发送过来的数据
        DatagramPacket   dp_receive = new DatagramPacket(buf, 160000);
        System.out.println("server is on，waiting for client to send data......");
        boolean f = true;
        while(f){
            //服务器端接收来自客户端的数据
            ds.receive(dp_receive);
            System.out.println("server received data from client：");
            String str_receive = new String(dp_receive.getData(),0,dp_receive.getLength()) ;
            System.out.println(" from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort());
            Base base = new ToTyphoon().toBase(str_receive);
            System.out.println(new TyphoonSave().Save(base));
            //数据发动到客户端的9000端口
            DatagramPacket dp_send= new DatagramPacket(str_send.getBytes(),str_send.length(),dp_receive.getAddress(),9000);
            ds.send(dp_send);
            //由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
            //所以这里要将dp_receive的内部消息长度重新置为160000
            dp_receive.setLength(160000);

        }
        ds.close();
    }
}
