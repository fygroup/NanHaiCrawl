package com.ab.crawl.util;


import java.sql.*;

/** 
* @ClassName: DBUtil 
* @Description: 数据库连接
* @author gaow
* @date 2018年10月30日 下午4:33:15 
*  
*/
public class MakeHtmlUtil {
	
    private static final String content_first ="<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head id=\"Head1\"><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>\n" +
            "</title><script language=\"javascript\">\n" +
            "    var j = 0; var timer; var interval = 1000;\n" +
            "    var total = 73;\n" +
            "    var arr = new Array(total);";
    private static final String content_second ="    //自动播放、暂停\n" +
            "    function change(param) {\n" +
            "    interval = Form1.play_inter.value * 1000;\n" +
            "\n" +
            "        if (j >= total || j < 0)\n" +
            "            j = 0;\n" +
            "        if (param == \"auto\") {\n" +
            "            document.getElementById(\"image1\").src = arr[j];\n" +
            "            timer = setTimeout(\"change('auto')\", interval);\n" +
            "            document.getElementById(\"auto\").style.display = \"none\";\n" +
            "            document.getElementById(\"pause\").style.display = \"\";\n" +
            "            j++;\n" +
            "        } else {\n" +
            "            document.getElementById(\"auto\").style.display = \"\";\n" +
            "            document.getElementById(\"pause\").style.display = \"none\";\n" +
            "            clearTimeout(timer);\n" +
            "        }\n" +
            "\n" +
            "    }\n" +
            "    //显示选择的图片\n" +
            "    function show(index) {\n" +
            "        j = index;\n" +
            "        document.getElementById(\"image1\").src = arr[index];\n" +
            "        document.getElementById(\"auto\").style.display = \"\";\n" +
            "        document.getElementById(\"pause\").style.display = \"none\";\n" +
            "        clearTimeout(timer);\n" +
            "    }\n" +
            "\n" +
            "    function nextimg() {\n" +
            "        j = j + 1;\n" +
            "        if (j > total - 1) j = 0;\n" +
            "        show(j);\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "    function upimg() {\n" +
            "        j = j - 1;\n" +
            "        if (j < 0) j = total - 1;\n" +
            "        show(j);\n" +
            "\n" +
            "    }\n" +
            "    //设置播放时间间隔\n" +
            "    function setShowInterval(value) {\n" +
            "        interval = value * 1000;\n" +
            "    }\n" +
            "    \n" +
            "    function bigimg() {\n" +
            "       document.getElementById(\"image1\").width = document.getElementById(\"image1\").width * 1.1;\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "    function smallimg() {\n" +
            "       document.getElementById(\"image1\").width = document.getElementById(\"image1\").width * 0.9;\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "</script></head>\n" +
            "\n" +
            "\n" +
            "<body bgcolor=\"#FFFFFF\" text=\"#000000\">\n" +
            "<form method=\"post\" action=\"http://szyb.hyyb.org/bigimg_show.aspx?id=36\" id=\"Form1\">\n" +
            "<div class=\"aspNetHidden\">\n" +
            "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"/wEPDwUJNTM1MjgyMTMyZGQVDC/Hn8uRjlwpSqaEGtbho0K8jWt7kAuZ7eJ6qLB5NQ==\">\n" +
            "</div>\n" +
            "\n" +
            "<div id=\"title\" style=\"width: 600px;height: 0px;TEXT-ALIGN: left;PADDING-TOP: 10px ;FONT-SIZE:20px;\"></div>\n" +
            "<div class=\"tab_001_001\">\n" +
            "<ul id=\"tags_sub\"></ul><!--图片显示层-->\n" +
            "<div id=\"tagContent\">\n" +
            "<div class=\"showtip_out\" onmouseover=\"this.className=&#39;showtip_hover&#39;\" onmouseout=\"this.className=&#39;showtip_out&#39;\">\n" +
            "<div id=\"imgplay\"><select id=\"play_inter\" onchange=\"setShowInterval(this.value)\">\n" +
            "<option selected=\"\" value=\"1\">1秒</option>\n" +
            "<option value=\"2\">2秒</option>\n" +
            "<option value=\"3\">3秒</option>\n" +
            "<option value=\"4\">4秒</option>\n" +
            "<option value=\"5\">5秒</option> \n" +
            "</select>\n" +
            "       <input type=\"button\" value=\"自动\" onclick=\"change(&#39;auto&#39;)\" id=\"auto\"><input type=\"button\" value=\"暂停\" onclick=\"change(pause)\" id=\"pause\" style=\"display:none\"> <input type=\"button\" name=\"Submit\" value=\"上一张\" onclick=\"javascript:upimg();\"> <input type=\"button\" name=\"Submit\" value=\"下一张\" onclick=\"javascript:nextimg();\"> \n" +
            "\t   <select onchange=\"show(this.value)\">\n" ;

    private static final String content_third ="        </select> <input type=\"button\" value=\"放大\" onclick=\"bigimg()\" id=\"Button1\">  <input type=\"button\" value=\"缩小\" onclick=\"smallimg()\" id=\"Button2\">\n" +
            "</div>\n" +
            "<div style=\"DISPLAY: none; FLOAT: left; COLOR: red\" id=\"loadingTip\">正在加载图片中……</div>\n" +
            "<div style=\"WIDTH: auto\" id=\"othertool\">\n" +
            " </div></div>\n" +
            "<div><!--startimage-->\n" +
            "<div id=\"tagContent1\"> \n" +
            "   <img src=\"";
    private static final String content_final ="\" id=\"image1\" width=\"811\">\n" +
            "</div><!--endimage--></div><!--文字显示层--><!--starttext-->\n" +
            "<div id=\"txtContent1\"></div><!--endtext-->\n" +
            "</div></div>\n" +
            "</form>\n" +
            "\n" +
            "\n" +
            "</body></html>";

    /** 
    * @Title: getConn 
    * @Description: 业务库
    * @param @return    设定文件 
    * @return Connection    返回类型 
    * @throws 
    */
    public static String makeHtml(String str_first,String str_second,String str_third) {

        return content_first+str_first+content_second+str_second+content_third+str_third+content_final;
    }


}
