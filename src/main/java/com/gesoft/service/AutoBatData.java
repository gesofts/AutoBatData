package com.gesoft.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.gesoft.config.Config;
import com.gesoft.thread.TaskThread;
import com.gesoft.utils.LogUtil;

public class AutoBatData
{

	private int delay = 1; //延迟时间为小时
	private int expCnt = 0; //执行备份的次数
	private List<String> expFlys = new ArrayList<String>();
	
	public static void main(String[] args)
	{
		AutoBatData mAutoBatData  = new AutoBatData();
		mAutoBatData.init();
	}

	
	private void init()
	{
		String rootpath = System.getProperty("user.dir");
		Config.setConfigFileName(rootpath+File.separatorChar+"app.properties");
		try
		{
			expCnt = Integer.parseInt(Config.getInstance().getProperty("EXP_CNT"));
			delay = Integer.parseInt(Config.getInstance().getProperty("EXP_DELAY")); 
			for (int nItem = 1; nItem <= expCnt; nItem++)
			{
				expFlys.add(Config.getInstance().getProperty(String.format("EXP_PATH%d", nItem)));
			}
		}
		catch (Exception e) 
		{
			LogUtil.getLogger().error(e.getMessage());
		}
		start();
	}
	
	private void start()
	{
		ScheduledThreadPoolExecutor mScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2); 
		for (int nItem = 0; nItem < expFlys.size(); nItem++)
		{
			mScheduledThreadPoolExecutor.scheduleWithFixedDelay(new TaskThread(expFlys.get(nItem)), 0, delay, TimeUnit.HOURS);
		}
	}
	
	
}
