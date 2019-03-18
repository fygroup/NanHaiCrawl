package com.ab.crawl.util;


import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Set;

public class FileUtil {

    private static final String configDir = SetSystemProperty.getKeyValue("dir");
    public boolean NewFile(String text,String name) {
        boolean flag =false;
        try {

            /* 写入Txt文件 */
            File writename = new File(configDir+name); // 相对路径，如果没有则要建立一个新的output。txt文件
            writename.createNewFile();
            // 创建新文件
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write(text);
            // 把缓存区内容压入文件
            out.flush();
            // 最后记得关闭文件
            out.close();
            flag = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static void main(String args[]) {

        /* 读入TXT文件 */
            /*String pathname = "D:\\twitter\\13_9_6\\dataset\\en\\input.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(pathname); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            line = br.readLine();
            while (line != null) {
                line = br.readLine(); // 一次读入一行数据
            }*/

            new FileUtil().NewFile("hhaa","announcement/123");
    }


}
