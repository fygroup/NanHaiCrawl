package com.ab.crawl.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Set;

import static org.apache.log4j.helpers.LogLog.error;

public class FtpUtil {
    private static FtpUtil ftpUtil ;
    //ftp对象
    private FTPClient ftp;
    //private static final String ip = PropertiesConfig.INSTANCE.getProPerties("ftpip");
    private static final String ip =SetSystemProperty.getKeyValue("ftpip");
    private static final String port = SetSystemProperty.getKeyValue("ftpport");
    private static final String name = SetSystemProperty.getKeyValue("ftpname");
    private static final String pwd = SetSystemProperty.getKeyValue("ftppwd");
    private static final String configDir = SetSystemProperty.getKeyValue("dir");
    private static final String configProperties =SetSystemProperty.getKeyValue("configProperties");

    //调用此方法，输入对应得ip，端口，要连接到的ftp端的名字，要连接到的ftp端的对应得密码。连接到ftp对象，并验证登录进入ftp
    public FtpUtil() {
        ftp = new FTPClient();

        //验证登录
        try {

            ftp.connect(ip, Integer.parseInt(port));
            ftp.login(name, pwd);
            ftp.setCharset(Charset.forName("UTF-8"));
            ftp.setControlEncoding("UTF-8");
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();

        } catch (IOException e) {
            System.out.println("ftp参数错误");
            e.printStackTrace();
        }

    }
    public static FtpUtil getInstance(){
        if (ftpUtil ==  null) {
            ftpUtil = new FtpUtil();
        }
        return ftpUtil;
    }

    //上传文件
    public void wavePutFile(String name) {
        try {
            //将本地的"D:/1.zip"文件上传到ftp的根目录文件夹下面，并重命名为"aaa.zip"
            ftp.storeFile("\\product\\WW3\\SCS\\"+name, new FileInputStream(new File(configDir+"\\product\\WW3\\SCS\\"+name)));
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
    public void windPutFile(String name) {
        try {
            //将本地的"D:/1.zip"文件上传到ftp的根目录文件夹下面，并重命名为"aaa.zip"
            ftp.storeFile("\\product\\WRF\\SCS\\"+name, new FileInputStream(new File(configDir+"\\product\\WRF\\SCS\\"+name)));
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public void putFile(String name) {
        try {
            //将本地的"D:/1.zip"文件上传到ftp的根目录文件夹下面，并重命名为"aaa.zip"
            ftp.storeFile(name, new FileInputStream(new File(configDir+name)));
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }



    //第二种方法
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
            OutputStream os = ftp.storeFileStream("/"+name);
           //DataOutputStream out = new DataOutputStream(new FileOutputStream("configDir"+fileLocal));
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = fis.read(b)) != -1) {
                os.write(b, 0, len);
            }
        } catch (IOException e) {
            System.out.println("上传附件失败");
            e.printStackTrace();
        }
    }

    //更新配置文件，如果有修改需求 ，可以更改application.properties中的信息
    public void loadFile() {
        String localFileName = configDir+configProperties;
        String remoteFileName = configProperties;
        BufferedOutputStream buffOut = null;
        try {
            buffOut = new BufferedOutputStream(new FileOutputStream(localFileName));
            /** 写入本地文件*/
            ftp.retrieveFile(remoteFileName, buffOut);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (buffOut != null) {
                    buffOut.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String args[]) {
        FtpUtil ftpUtil = new FtpUtil();
        //上传配置文件
        //ftpUtil.putFile(configProperties);
        //下载配置文件
        ftpUtil.loadFile();
    }

}
