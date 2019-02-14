package com.ab.crawl.util;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/01/23 17:03
 * @see com.ab.crawl.util
 */
public class UrlFile {

    private static final String regex = "";
    private static final String configDir = PropertiesConfig.INSTANCE.getProPerties("dir");
    /**
     * 下载文件到本地
     * @author nadim
     * @date Sep 11, 2018 11:45:31 AM
     * @param fileUrl 远程地址
     * @throws Exception
     */
    public String  downloadFile(String fileUrl) throws Exception {
        URL url = new URL(fileUrl);
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
        urlCon.setConnectTimeout(6000);
        urlCon.setReadTimeout(6000);
        int code = urlCon.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            return "";
            //throw new Exception("文件读取失败");
        }
        //读文件流
        DataInputStream in = new DataInputStream(urlCon.getInputStream());
        //文件路径
        String localDir = "";
        //文件夹名+文件名
        String file = "";
        if (regex(fileUrl)){
            System.out.println("将被分解的字符串 "+fileUrl);
            String[] fileName = fileUrl.split("/",7);
            file = fileName[fileName.length-1];
            localDir = file.substring(0,file.lastIndexOf("/")+1);
            System.out.println("本地目录"+localDir);
            File outDir =new File(configDir+localDir);
            outDir.mkdirs();
        }else {
            System.out.println("非本网站网址，按文件名直接下载");
            String[] fileName = fileUrl.split("/");
            file = fileName[fileName.length-1];
        }

        DataOutputStream out = new DataOutputStream(new FileOutputStream(configDir+file));
        byte[] buffer = new byte[2048];
        int count = 0;
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        try {
            if(out!=null) {
                out.close();
            }
            if(in!=null) {
                in.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    public boolean regex(String text){


        String pattern = ".*www.gdsafety.gov.cn/gdyjglt/.*";

        boolean isMatch = Pattern.matches(pattern, text);
        System.out.println("字符串中是否包含了 'www.gdsafety.gov.cn/gdyjglt/' 子字符串? " + isMatch);
        return isMatch;
    }

}
