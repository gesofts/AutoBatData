package com.gesoft.thread;

import com.gesoft.utils.LogUtil;

public class TaskThread extends Thread
{

	private String filePath = "";
	public TaskThread()
	{
		
	}
	
	public TaskThread(String filePath)
	{
		this.filePath = filePath;
	}
	
	@Override
	public void run()
	{
		try
		{
			Process ps = Runtime.getRuntime().exec(String.format("cmd /c start %s", filePath));
			ps.waitFor();
		}
		catch (Exception e)
		{
			LogUtil.getLogger().error(String.format("TaskThread error :%s", e.getMessage()));
		}
		LogUtil.getLogger().info("数据库备份一次");
	}
}
