package com.ab.crawl;


import com.ab.crawl.quartz.Quartz;


/** 
* @ClassName: App 
* @Description: 程序执行入口
* @author tongzq
* @date 2019年11月12日 上午9:08:56
*/
public class App 
{
	//程序主入口 执行定时调度任务
	public static void main( String[] args )
	{
		System.out.println("正在运行爬取程序");

		new Quartz().quartzMain();
	}

}
