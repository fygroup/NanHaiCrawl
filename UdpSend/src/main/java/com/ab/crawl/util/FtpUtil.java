package com.ab.crawl.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Set;

public class FtpUtil {

    //ftp对象
    private FTPClient ftp;
    private static final String ip = PropertiesConfig.INSTANCE.getProPerties("ftpip");
    private static final String port = PropertiesConfig.INSTANCE.getProPerties("ftpport");
    private static final String name = PropertiesConfig.INSTANCE.getProPerties("ftpname");
    private static final String pwd = PropertiesConfig.INSTANCE.getProPerties("ftppwd");
    private static final String configDir = PropertiesConfig.INSTANCE.getProPerties("dir");

    //调用此方法，输入对应得ip，端口，要连接到的ftp端的名字，要连接到的ftp端的对应得密码。连接到ftp对象，并验证登录进入fto
    public FtpUtil() {
        ftp = new FTPClient();

        //验证登录
        try {
            ftp.enterLocalPassiveMode();
            ftp.connect(ip, Integer.parseInt(port));
            ftp.login(name, pwd);
            ftp.setCharset(Charset.forName("UTF-8"));
            ftp.setControlEncoding("UTF-8");
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

        } catch (IOException e) {

            System.out.println("ftp参数错误");
            e.printStackTrace();
        }

    }

//  //验证登录
//  public void login() {
//      try {
//          ftp.connect(ip, port);
//          System.out.println(ftp.login(name, pwd));
//          ftp.setCharset(Charset.forName("UTF-8"));
//          ftp.setControlEncoding("UTF-8");
//
//      } catch (IOException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//      }
//  }

    //获取ftp某一文件（路径）下的文件名字,用于查看文件列表
    public void getFilesName() {
        try {
            //获取ftp里面，“Windows”文件夹里面的文件名字，存入数组中
            FTPFile[] files = ftp.listFiles("/Windows");
            //打印出ftp里面，“Windows”文件夹里面的文件名字
            for (int i = 0; i < files.length; i++) {
                System.out.println(files[i].getName());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //上传文件
    public void wavePutFile(String name) {
        try {
            //将本地的"D:/1.zip"文件上传到ftp的根目录文件夹下面，并重命名为"aaa.zip"
            System.out.println(ftp.storeFile("\\product\\WW3\\NWP\\"+name, new FileInputStream(new File(configDir+"\\product\\WW3\\NWP\\"+name))));
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    public void windPutFile(String name) {
        try {
            //将本地的"D:/1.zip"文件上传到ftp的根目录文件夹下面，并重命名为"aaa.zip"
            System.out.println(ftp.storeFile("\\product\\WRF\\SCS\\"+name, new FileInputStream(new File(configDir+"\\product\\WRF\\SCS\\"+name))));
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public void putFile(String name) {
        try {
            //将本地的"D:/1.zip"文件上传到ftp的根目录文件夹下面，并重命名为"aaa.zip"
            System.out.println(name+"的putFile结果为"+ftp.storeFile(name, new FileInputStream(new File(configDir+name))));
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    //海风海浪上传
    public void putFile2(String name) {
        try {
            ftp.enterLocalPassiveMode();
            OutputStream os = ftp.storeFileStream(name);
            FileInputStream fis = new FileInputStream(new File(configDir+name));

            byte[] b = new byte[102400];
            int len = 0;
            while ((len = fis.read(b)) != -1) {
                os.write(b, 0, len);
            }
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    //上传附件（自动创建文件夹）
    public void putAnnex(String name){
        try {

            FileInputStream fis = new FileInputStream(new File(configDir+name));
            String localDir = "/"+name.substring(0,39);
            System.out.println(localDir);
            ftp.makeDirectory(localDir);
            boolean a = ftp.changeWorkingDirectory(localDir);
            System.out.println("转换目录结果1:"+a);
            OutputStream os = ftp.storeFileStream("/"+name);
           //DataOutputStream out = new DataOutputStream(new FileOutputStream("configDir"+fileLocal));
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = fis.read(b)) != -1) {
                os.write(b, 0, len);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("上传附件失败");
            e.printStackTrace();
        }
    }
    //下载文件
    public void getFile() {
        try {
            //将ftp根目录下的"jsoup-1.10.2.jar"文件下载到本地目录文件夹下面，并重命名为"1.jar"
            ftp.retrieveFile("jsoup-1.10.2.jar", new FileOutputStream(new File("D:/1.jar")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //下载文件的第二种方法，优化了传输速度
    public void getFile2() {
        try {
            InputStream is = ftp.retrieveFileStream("aaa.txt");

            FileOutputStream fos = new FileOutputStream(new File("D:/aaa.txt"));

            byte[] b = new byte[1024];
            int len = 0;
            while ((len = is.read(b)) != -1) {
                fos.write(b, 0, len);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block3
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {

        System.out.println(configDir);
        FtpUtil m = new FtpUtil();
        m.putFile2("53d9db1458dc448b816e9f76b164e32d.shtml");
        //new FtpUtil().putAnnex("4d5797119c0448e2a9b1ee84d756d9e0/images/90b8b59f7618425da123db42a6aea468.png");
    }
}