/**
 * 文件名称：Config.java
 * 版权所有：Copyright njty
 * 创建时间：2017年6月8日
 * 创 建 人：WCL (ln_admin@yeah.net)
 * 功能描述：
 **/
package com.gesoft.config;

/**
 * @author WCL
 * @version v1.001
 * @since   v1.001
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.gesoft.utils.DataTypeUtil;
import com.gesoft.utils.LogUtil;

/**
 *  配置文件类 <p>
 *
 *  在使用之前应该先设置configFileName <p>
 *
 *  modify by wjz 0312 ,为了同时支持Properties类型和XML类型的配置</p> 对于XML类型的配置，格式如下<p>
 *
 *  &lt;CONFIG&gt;<br>
 *  &lt;DESCRIPTION&gt;关于电子申报的配置文件&lt;/DESCRIPTION&gt;<br>
 *  &lt;ITEM KEY=dbaDBType&gt; <br>
 *  &lt;VALUE&gt;oracle.jdbc.driver.OracleDriver&lt;/VALUE&gt;<br>
 *  &lt;REMARK&gt;oracle驱动程序&lt;/REMARK&gt;<br>
 *  &lt;/ITEM&gt;<br>
 *  &lt;/CONFIG&gt;
 *
 *@author     william
 *@created    2003年4月22日
 */
public class Config {
    //add by wjz 0225
    /**
     *  Description of the Field
     */
    public static int CONFIG_PROPFILE = 1;
    /**
     *  Description of the Field
     */
    public static int CONFIG_XMLFILE = 2;

    private static Config cfg = null;
    private static String configFileName = null;
    //默认的配置文件类型为CONFIG_PROPFILE
    private int config_file_type = 1;
    private Document doc = null;
    //end of add by wjz

    private Properties props;
    private Node rootElement = null;
    private static String workPath = "";
    private Vector tempProps=new Vector();

    /**
     *  Constructor for the Config object
     */
    public Config() {
        props = new java.util.Properties();
    }


    /**
     *  Sets the ConfigFileName attribute of the Config object
     *
     *@param  cfg  The new ConfigFileName value
     */
    public static void setConfigFileName(String cfg) {
        configFileName = cfg;
        //LogUtil.setConfigFile(cfg);
    }

 public void setProperty(String keyName, String keyValue,boolean isTemp) {
   if(isTemp){
     tempProps.remove(keyName);
     tempProps.add(keyName);
   }
   setProperty(keyName,keyValue);

 }
    /**
     *  Sets the Property attribute of the Config object
     *
     *@param  keyName   The new Property value
     *@param  keyValue  The new Property value
     */
    public void setProperty(String keyName, String keyValue) {
        if (keyName == null || keyValue == null || keyName.length() == 0) {
            return;
        }
            props.setProperty(keyName, keyValue);
    }


    /**
     *  Gets the ConfigFileName attribute of the Config object
     *
     *@return    The ConfigFileName value
     */
    public static String getConfigFileName() {
        return configFileName;
    }


    /**
     *@return     SticConfig
     *@since      2001-12-25 if(cfg==null){ cfg=new SticConfig();
     *      cfg.loadConfig(); }else{ return cfg; }
     *@roseuid    3C2D9408001F
     */
    public static Config getInstance() {
        if (cfg == null) {
            cfg = new Config();
            cfg.loadConfig();
            return cfg;
        } else {
            return cfg;
        }

    }


    /**
     *  Gets the instance attribute of the Config class
     *
     *@param  configFileName  Description of the Parameter
     *@return                 The instance value
     */
    public static Config getInstance(String configFileName) {
        if (cfg == null) {
            cfg.setConfigFileName(configFileName);
            cfg = new Config();
            cfg.loadConfig();
            return cfg;
        } else {
            return cfg;
        }

    }


    /**
     *  Gets the Property attribute of the Config object
     *
     *@param  keyName  Description of Parameter
     *@return          The Property value
     */
    public String getProperty(String keyName) {
    	String s = props.getProperty(keyName);
        return DataTypeUtil.toCHNString(s);
    }


    /**
     *  取得系统的工作目录
     *
     *@return
     */
    public final static String getWorkPath() {
        if (workPath == null || workPath.length() <= 0) {
            workPath = getInstance().getProperty("workDir");
        }
        if (workPath == null) {
            workPath = getInstance().getProperty("workdir");
        }
        return workPath;
    }


    /**
     *  从文件名为stol.properties读取配置
     *
     *@return     int
     *@roseuid    3C22E0060356
     */
    private int loadConfig() {
        InputStream fis = null;
        try {
            try {
                if (configFileName == null || configFileName.length() <= 0) {
                    LogUtil.getLogger().error("配置文件为空！");
                    return -1;
                }
                fis = Config.class.getResourceAsStream(configFileName);
                props.load(fis);
            } catch (Exception ex1) {
                /*
                 *  如果失败,直接从文件中取
                 */
                fis = new FileInputStream(configFileName);
                props.load(fis);
            }
            LogUtil.getLogger().debug("load config success:" + configFileName);
            return 0;
        } catch (Exception ex) {
            LogUtil.getLogger().debug("file may not exist");
            LogUtil.getLogger().debug(this.getClass().getName() + "::", ex);

            return 100;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception ex2) {
            }
        }
    }


    /**
     *  The main program for the SticConfig class
     *
     *@param  args  The command line arguments
     */
    public static void main(String[] args) {

//        Config sc = Config.getInstance("F:/new_source/GANSU/stic/stic.xml");

        Properties p= new Properties();
        try {
			p.load(new FileInputStream("F:/new_source/GANSU/stic/stic2.properties"));
			for (Iterator iter = p.keySet().iterator(); iter.hasNext();) {
				String element = (String) iter.next();

					String ss = p.getProperty(element);
					if("CLIENT_FONT_NAME".equals(element)){

					ss = new String(ss.getBytes("8859_1"),"GB2312");
					System.out.print("ss:"+ss);
					}
//				sc.setProperty(element, ss);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        zxt.pub.util.LogUtil.getLogger().debug(p);
//        sc.setProperty("dbaDBURL_Ctais3", "jdbc:oracle:thin:@192.168.0.4:1521:ctais106");
//        System.err.println(sc.getProperty("dbaDBURL_Ctais"));
//        System.out.println(sc.getProperty("APPNAME"));
//        sc.setProperty("APPNAME", "北京市中兴通电子技术有限责任公司");
//        sc.saveConfig();
    }


    /**
     *  保存配置文件.配置文件名为STIC.properties
     *
     *@return     int
     *@roseuid    3C22E0060355
     */
    public int saveConfig() {
        java.io.FileOutputStream fos = null;
        try {
//            zxt.pub.util.Utilities.setUserDir();//yql 2004-01-08 rem
            if (configFileName == null || configFileName.length() <= 0) {
                return -1;
            }
            fos = new java.io.FileOutputStream(configFileName);
            props.store(fos, "STIC CONFIG FILE");
            fos.close();
            return 0;
        } catch (Exception ex) {
            LogUtil.getLogger().debug("file may not exist");
            LogUtil.getLogger().debug(this.getClass().getName() + "::", ex);
            ex.printStackTrace();
            return 100;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception ex2) {
            }
        }

    }

	public int saveConfig(String fileName){
		if(fileName == null){
			LogUtil.getLogger().error("要保存的文件名为空,保存出错!");
			return 100;
		}
		if(fileName.toLowerCase().endsWith(".xml")){
			config_file_type = Config.CONFIG_XMLFILE;
		}else{
			config_file_type = Config.CONFIG_PROPFILE;
		}
		configFileName = fileName;
		return saveConfig();
	}
}
