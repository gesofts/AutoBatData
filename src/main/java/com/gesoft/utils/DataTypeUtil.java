/**
 * 文件名称：DataTypeUtil.java
 * 版权所有：Copyright njty
 * 创建时间：2017年6月8日
 * 创 建 人：WCL (ln_admin@yeah.net)
 * 功能描述：
 **/
package com.gesoft.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;


/**
 * 关于数据类型处理的类
 * <p>
 * wjz,0226,增加findMapValueByKey方法
 * 
 * @author william
 * @created 2003年8月31日
 */
public final class DataTypeUtil {

	public static int bytesToInt(byte[] b) {

		int mask = 0xff;
		int temp = 0;
		int n = 0;
		for (int i = 0; i < b.length; i++) {
			n <<= 8;
			temp = b[i] & mask;
			n |= temp;
		}
		return n;
	}
	
	
	public static long bytesToLong(byte[] b) {

		int mask = 0xff;
		int temp = 0;
		long n = 0;
		for (int i = 0; i < b.length; i++) {
			n <<= 8;
			temp = b[i] & mask;
			n |= temp;
		}
		return n;
	}
	
	
	public static String IntToHex(int i, int length) {
		String temp = Integer.toHexString(i);
		while (temp.length()<length*2) {
			temp = "0" + temp;
		}
		return temp;
	}
	
	public static int HexToInt(String hex) {
		return Integer.parseInt(hex, 16);
	}
	
	public static String bytesToBinary(byte[] b, int byteLength) {
		int i = bytesToInt(b);
		String binString = Integer.toBinaryString(i);
		while (binString.length() < byteLength*8) {
			binString = "0" + binString;
		}
		return binString;
	}
	
	public static String binaryToInt(String binary, int intlength){
		String s = Integer.parseInt(binary,2)+"";
		while ((s+"").length()<intlength){
			s = "0"+s;
		}
		return s;
	}
	
	public static byte[] getSrcData(byte[] b) {
		return b;
	}
	
	public static String getSrcData(String hex) {
		return hex;
	}
	
	public static byte[] getLittleEndian(byte[] b) {
		byte[] temp = new byte[b.length];
		for (int i = 0; i < b.length; i++) {
			temp[b.length-i-1] = b[i];
		}
		return temp;
	}
	
	public static String getLittleEndian(String hex) {
		int len = hex.length();
		if (len%2==1)return null;
		String str = "";
		for (int i = len/2; i > 0; i--) {
			str += hex.substring((i-1)*2, i*2);
		}
		return str;
	}
	
	/**
	 * 构造函数
	 */
	public DataTypeUtil() {
	}

	/**
     * 将字节转换成无符号的整数字符串形式
     * @param src
     * @return
     */
    public static String byte2Hexs(byte src) {
        try {
        	String stmp = (java.lang.Integer.toHexString(src & 0XFF));
            if (stmp.length() == 1) {
            	stmp = "0" + stmp;
            }
            return stmp.toUpperCase();
        }
        catch (java.lang.NullPointerException ex) {
            return null;
        }
    }
    
	/**
	 * 判断child是否是parent的子类
	 * 
	 * @param parent
	 *            Class
	 * @param child
	 *            Class
	 * @return boolean
	 */
	public static boolean isChildClass(Class parent, Class child) {

		if (child == null || parent == null)
			return false;
		Class cls = child;
		while (!cls.getName().equals(parent.getName())) {
			cls = cls.getSuperclass();
			if (cls == null)
				return false;
		}
		return true;
	}
	
	public static Object invokeMethod(String className, String methodName,

	Object[] args) {
		return invokeMethod(className, methodName, null, args);
	}

	/**
	 * 调用Java的方法
	 * <p>
	 * add by wjz 040825
	 * 
	 * @param className
	 *            String 类名称（包括Package）
	 * @param methodName
	 *            String 方法名称
	 * @param args
	 *            Object[] 调用参数
	 * @return Object
	 */
	public static Object invokeMethod(String className, String methodName,
			Class[] argsType, Object[] args) {
		try {
			Class cls = Class.forName(className);
			Object obj = null;
			if (cls == null)
				return obj;
			Constructor classConstructor = cls.getConstructor(null);

			Object clsObj = classConstructor.newInstance(null);
			// 有参数类型，直接调用
			if (argsType != null) {
				Method m = cls.getMethod(methodName, argsType);
				if (m == null)
					return null;
				obj = m.invoke(clsObj, args);
				if (obj == null)
					return "true";
				return obj;
			}
			Class argCls[];
			// 如果只有一个同名的，直接调用
			int count = 0;
			Method m = null;
			Method[] ma = cls.getMethods();
			if (ma == null || ma.length == 0)
				return null;
			for (int i = 0; i < ma.length; i++) {
				if (ma[i] == null || !ma[i].getName().equals(methodName)) {
					continue;
				}
				m = ma[i];
				count++;

			}
			if (count == 1) {
				obj = m.invoke(clsObj, args);
				if (m.getReturnType() == null && obj == null)
					return "true";
				return obj;

			}
			// 存在多个同名函数，需要依次判断
			for (int i = 0; i < ma.length; i++) {
				if (ma[i] == null || !ma[i].getName().equals(methodName)) {
					continue;
				}
				argCls = ma[i].getParameterTypes();
				if ((argCls == null || argCls.length == 0 || args == null || args.length == 0)) {
					obj = ma[i].invoke(clsObj, args);
					if (ma[i].getReturnType() == null && obj == null)
						return "true";
					return obj;

				} else {
					// 参数长度和类型不一致,则不调用
					if (argCls.length != args.length)
						continue;
					int j;
					for (j = 0; j < argCls.length; j++) {
						if (args[j] == null || args[j].getClass() == null)
							break;
						if (!isChildClass(argCls[j], args[j].getClass())) {
							break;
						}
					}
					if (j != argCls.length) {
						continue;
					}

					obj = ma[i].invoke(clsObj, args);
					if (ma[i].getReturnType() == null && obj == null)
						return "true";
					return obj;

				}

			}

			return obj;
		} catch (Throwable ex) {
			LogUtil.getLogger().error(ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * 岳老虎 2004-01-13 添加此方法
	 * 
	 * @param className
	 *            类的名称
	 * @param argsType
	 *            参数名称
	 * @param argsValue
	 *            参数值
	 * @return 返回实例华以后的对象
	 */
	public static Object getClassInstance(String className, Class[] argsType,
			Object[] argsValue) {
		try {
			Class dynamicClass = Class.forName(className);
			Constructor classConstructor = dynamicClass
					.getConstructor(argsType);
			Object objInstance = classConstructor.newInstance(argsValue);
			return objInstance;
		} catch (Exception ex) {
			LogUtil.getLogger().error(ex.getMessage(), ex);
			return null;
		}
	}
	

	// add by hejiang 20030725
	/**
	 * Gets the DateStr attribute of the DataTypeUtil class
	 * 
	 * @return The DateStr value
	 */
	public static synchronized String getDateStr() {
		java.util.Date now = new java.util.Date();
		String s = DateUtil.getStringDate("yyyy-MMdd-HHmmss");
		return s;
	}

	// add by hejiang

	/**
	 * 产生8位随机数（字符串）
	 * <p>
	 * wujinzhong 0530
	 * <p>
	 * 
	 * @return 对应的整数值,如果出错,则返回Integer.MIN_VALUE
	 */

	public static synchronized String getRandomStr() {
		java.util.Date now = new java.util.Date();
		String s = DateUtil.getStringDate("yyyyMMddHHmmssSSS");
		try {
			Thread.sleep(1);
		} catch (Exception ex) {
			LogUtil.getLogger()
					.error("FATAL ERROR! get random string fail", ex);
		}
		return s;
		// return Long.toString(now.getTime());
		// return String.valueOf((int) (Math.random() * 100000000));

	}

	/**
	 * 对Hashtable的键值排序 wjz 0830
	 * 
	 * @param ht
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */

	public static Object[] getSortedKey(Hashtable ht) {
		if (ht == null) {
			return null;
		}

		try {
			Vector v = new Vector(ht.keySet());
			Collections.sort(v);
			Object[] keys = v.toArray();
			return keys;
		} catch (Exception ex) {
			LogUtil.getLogger().error("sort error", ex);
			return ht.keySet().toArray();
		}

	}

	// end by add

	/**
	 * 产生8位随机数（整形）
	 * 
	 * @param bt1
	 *            Description of Parameter
	 * @param bt2
	 *            Description of Parameter
	 * @return 对应的整数值,如果出错,则返回Integer.MIN_VALUE
	 */
	// public static int getRandomInt() {
	// return (int) (Math.random() * 100000000);
	// }
	// end by add
	/**
	 * 产生8位随机数（整形） 产生8位随机数（整形） 产生8位随机数（整形） 产生8位随机数（整形） 产生8位随机数（整形） 产生8位随机数（整形）
	 * 产生8位随机数（整形） 产生8位随机数（整形） 产生8位随机数（整形） 判断两个字节数组是否相等
	 * 
	 * @param bt1
	 *            Description of Parameter
	 * @param bt2
	 *            Description of Parameter
	 * @return 对应的整数值,如果出错,则返回Integer.MIN_VALUE
	 * @return The BytesEquals value
	 */
	public static boolean isBytesEquals(byte bt1[], byte bt2[]) {
		if (bt1.length != bt2.length) {
			return false;
		}
		for (int i = 0; i < bt1.length; i++) {
			if (bt1[i] != bt2[i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * obj的字符串表示形式是否为空字符串（null或"")
	 * <p>
	 * add by wjz 040401
	 * 
	 * @param obj
	 *            目标对象
	 * @return 其字符串形式为null或""返回为true,否则为false
	 */
	public static boolean isEmptyStr(Object obj) {
		if (obj == null) {
			return true;
		}
		try {
			String s = obj.toString();
			if (s == null || s.trim().length() == 0) {
				return true;
			}
			return false;
		} catch (Exception ex) {
			return false;
		}

	}

	/**
	 * 字节数组反向查找
	 * 
	 * @param source
	 *            Description of Parameter
	 * @param search
	 *            Description of Parameter
	 * @param start
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static int byteBackwardIndexOf(byte source[], byte search[],
			int start) {
		int iRet = -1;
		int i = start;
		do {
			if (i <= 0) {
				break;
			}
			if (i - search.length < 0) {
				iRet = -1;
				break;
			}
			if (isBytesEquals(subBytes(source, (i - search.length) + 1, i),
					search)) {
				iRet = (i - search.length) + 1;
				break;
			}
			i--;
		} while (true);
		return iRet;
	}

	/**
	 * 字节数组查找
	 * 
	 * @param source
	 *            Description of Parameter
	 * @param search
	 *            Description of Parameter
	 * @param start
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static int byteIndexOf(byte source[], byte search[], int start) {
		int iRet = -1;
		int i = start;
		do {
			if (i >= source.length) {
				break;
			}
			if (i + search.length > source.length) {
				iRet = -1;
				break;
			}
			if (isBytesEquals(subBytes(source, i, (i + search.length) - 1),
					search)) {
				iRet = i;
				break;
			}
			i++;
		} while (true);
		return iRet;
	}

	/**
	 * 为一个二维数组增加或减少列
	 * 
	 * @author wujinzhong 050530
	 * @param data
	 *            Object[][]
	 * @param cloumnsCount
	 *            int 增加或减少的列数，正数表示增加列，负数表示减少列
	 * @return Object[][]
	 */
	public static Object[][] addArrayColumns(Object[][] data, int cloumnsCount) {
		if (data == null)
			return null;
		if (cloumnsCount == 0)
			return data;
		int col_len = data[0].length + cloumnsCount;
		int row_len = data.length;
		Object[][] data2 = new Object[row_len][col_len];
		int copy_col_len;// 实际要拷贝的列数
		if (cloumnsCount >= 0) {
			copy_col_len = data[0].length;
		} else {
			copy_col_len = col_len;
		}
		for (int i = 0; i < row_len; i++) {
			System.arraycopy(data[i], 0, data2[i], 0, copy_col_len);
		}

		return data2;
	}

	/**
	 * 将字节数组转换为16进制字符串如 {10,-1}-->0aFF
	 * 
	 * @param src
	 *            源数组
	 * @return 字符串,null表示失败
	 * @roseuid 3C15CF9502F8
	 */
	public static String bytes2Hexs(byte[] src) {

		StringBuffer hs = new StringBuffer();
		String stmp = "";
		hs.setLength(src.length * 2);
		int t = 0;
		try {

			for (int n = 0; n < src.length; n++) {
				stmp = (java.lang.Integer.toHexString(src[n] & 0XFF));
				if (stmp.length() == 1) {
					hs.setCharAt(t, '0');
					t++;
					hs.setCharAt(t, stmp.charAt(0));
					t++;
				} else {
					hs.setCharAt(t, stmp.charAt(0));
					t++;
					hs.setCharAt(t, stmp.charAt(1));
					t++;
				}
			}
		} catch (java.lang.NullPointerException ex) {
			return null;
		}
		String strTemp = hs.toString().toUpperCase();
		hs = null;
		return strTemp;
	}

	/**
	 * 将字节数组转换为16进制字符串如 {10,-1}-->0aFF
	 * 每个字节后面一个空格
	 * @param src
	 *            源数组
	 * @return 字符串,null表示失败
	 * @roseuid 3C15CF9502F8
	 */
	public static String bytes2HexsSpace(byte[] src) {

		StringBuffer hs = new StringBuffer();
		String stmp = "";
		hs.setLength(src.length * 3);
		int t = 0;
		try {

			for (int n = 0; n < src.length; n++) {
				stmp = (java.lang.Integer.toHexString(src[n] & 0XFF));
				if (stmp.length() == 1) {
					hs.setCharAt(t, '0');
					t++;
					hs.setCharAt(t, stmp.charAt(0));
					t++;
				} else {
					hs.setCharAt(t, stmp.charAt(0));
					t++;
					hs.setCharAt(t, stmp.charAt(1));
					t++;
				}
				hs.setCharAt(t, ' ');
				t++;
			}
		}
		// try
		catch (java.lang.NullPointerException ex) {
			return null;
		}
		String strTemp = hs.toString().toUpperCase();
		hs = null;
		return strTemp;
	}
	/**
	 * 字节数组拷贝
	 * 
	 * @param source
	 *            源字节数组
	 * @param dest
	 *            目标字节数组
	 * @param len
	 *            拷贝长度
	 * @return 0－成功 >0表示失败,值为对应ERROR_CODE
	 * @roseuid 3C183B36025A
	 */
	public static int bytesCopy(byte[] source, byte[] dest, int len) {
		try {
			for (int i = 0; i < len; i++) {
				dest[i] = source[i];
			}

			return 0;
		} catch (Exception ex) {
			LogUtil.getLogger().error(
					"ByteProcess_Java" + ":bytesCopy:" , ex);

			return 100;
		}
	}

	/**
	 * 从HashMap中查找某个keyd的值,支持递归查找
	 * <p>
	 * add by wjz 0226
	 * 
	 * @param map
	 *            Description of the Parameter
	 * @param key
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static Object findMapValueByKey(Map map, Object key) {
		if (map == null || key == null) {
			return null;
		}
		Object[] keys = map.keySet().toArray();
		if (keys == null) {
			return null;
		}
		int len = keys.length;
		Object key2 = null;
		for (int i = 0; i < len; i++) {
			key2 = keys[i];
			if (key2 == null) {
				continue;
			}
			if (key2.equals(key)) {
				return map.get(key2);
			}
			Object v1 = map.get(key2);
			if (v1 != null && v1 instanceof Map) {
				Object v2 = findMapValueByKey((Map) v1, key);
				if (v2 != null) {
					return v2;
				}
			}
		}
		return null;
	}

	/**
	 * 将16进制字符串转换为字节数组如 0aFF-->{10,-1}
	 * 
	 * @param src
	 *            字符串
	 * @return 目标数组,null表示失败
	 * @roseuid 3C15D0230126
	 */
	public static byte[] hexs2Bytes(java.lang.String src) {
		try {

			int len = src.length() / 2;
			int itemp;
			String stmp;
			if (src.length() % 2 == 1) {
				return null;
			}
			byte[] bt = new byte[len];

			for (int i = 0; i < len; i++) {
				stmp = src.substring(i * 2, i * 2 + 2);
				// itemp = Integer.decode("0x" + stmp).intValue() - 256;
				itemp = Integer.decode("0x" + stmp).intValue();
				bt[i] = (byte) itemp;

			}
			return bt;
		} catch (java.lang.NumberFormatException e) {
			LogUtil.getLogger().error(
					"ByteProcess_Java" + ":hexs2Bytes:", e);
			return null;
		}
	}

	// 把字符串用MD5加密
	public static String MD5(String src) {
		MessageDigest md = null;
		String des = "";
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			LogUtil.getLogger().error("MD5 error" , e);
		}
		md.update(src.getBytes());
		byte[] b = md.digest();
		des = bytes2Hexs(b);
		return des;
	}
	
	
	
	public static String MD5(byte[] en_byte) {
    	MessageDigest md = null;
    	String des = "";
    	try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			LogUtil.getLogger().error("MD5 error" + e.getMessage());
		}
		md.update(en_byte);
        byte[] b = md.digest();
        des = bytes2Hexs(b);
        return des;
    }
	

	/**
	 * The main program for the DataTypeUtil class
	 * 
	 * @param args
	 *            The command line arguments
	 */
	public static void main(String[] args) {
		//System.out.println(MD5(ManageFile.loadFromFile("D:/燃耗车辆范例.doc")));
	}

	/**
	 * 根据数据类型sjlx,对SRC进行处理
	 * 
	 * @param src
	 *            Description of Parameter
	 * @param errCode
	 *            Description of Parameter
	 * @param sjlx
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */

	public static byte[] manageBytes(byte[] src, StringBuffer errCode,
			String sjlx) {
		byte[] bt2 = src;
		if (sjlx.substring(0, 1).equals("1")) {
			return null;
		}
		if (sjlx.substring(2, 3).equals("1")) {
			return null;
		}
		return bt2;
	}

	/**
	 * 取得一组数中的最大值
	 * <p>
	 * modified by wjz 1005:change intValue to doubleValue
	 * </p>
	 * 
	 * @param iArr
	 *            Description of Parameter
	 * @return
	 */
	public static Number maxNum(Number iArr[]) {
		if (iArr == null || iArr.length <= 0) {
			return null;
		}
		Number rtn = iArr[0];
		for (int i = 1; i < iArr.length; i++) {
			if (rtn.doubleValue() > iArr[i].doubleValue()) {
				continue;
			}
			if (rtn.doubleValue() < iArr[i].doubleValue()) {
				rtn = iArr[i];
				continue;
			}
		}
		return rtn;
	}

	/**
	 * 取得一组数中的最小值
	 * <p>
	 * modified by wjz 1005:change intValue to doubleValue
	 * </p>
	 * 
	 * @param iArr
	 *            Description of Parameter
	 * @return
	 */
	public static Number minNum(Number iArr[]) {
		if (iArr == null || iArr.length <= 0) {
			return null;
		}
		Number rtn = iArr[0];
		for (int i = 1; i < iArr.length; i++) {
			if (rtn.doubleValue() < iArr[i].doubleValue()) {
				continue;
			}
			if (rtn.doubleValue() > iArr[i].doubleValue()) {
				rtn = iArr[i];
				continue;
			}
		}
		return rtn;
	}

	/**
	 * 打印字符数组 {10,FF} type=0打印10：-1 type=1打印0A：FF
	 * 
	 * @param src
	 *            将要打印的数组
	 * @param type
	 *            0-打印字符的十进制值，1－打印字符的十六进制值
	 * @return 结果字符串
	 * @roseuid 3C183E6F030A
	 */
	public String printCharArray(String src, int type) {
		return null;
	}

	/**
	 * 打印对象的内容
	 * <p>
	 * wujinzhong 0604,040922
	 * </P>
	 * 
	 * @param obj
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static String printObject(Object obj) {
		StringBuffer sb = new StringBuffer();
		if (obj == null)
			return "null";
		if (obj instanceof HashMap) {
			HashMap map = (HashMap) obj;
			Iterator keys = map.keySet().iterator();
			while (keys.hasNext()) {
				sb.append("\t");
				Object key = keys.next();
				sb.append("key=" + key);
				sb.append(printObject(map.get(key)));
				sb.append("\n");
			}
			return sb.toString();
		} else if (obj instanceof ArrayList) {

			ArrayList ary = (ArrayList) obj;
			Iterator values = ary.iterator();
			while (values.hasNext()) {
				sb.append("\t");
				sb.append(printObject(values.next()));
				sb.append("\n");
			}

			return sb.toString();
		} else if (obj instanceof byte[]) {
			byte[] bts = (byte[]) (obj);
			sb.append("size=" + bts.length + ",data={");
			for (int i = 0; i < bts.length; i++) {
				sb.append(bts[i] + ",");
			}
			sb.append("}");
			return sb.toString();
		} else {
			return obj.toString();
		}

	}

	/**
	 * 四舍五入算法
	 * 
	 * @param strNumber
	 *            四舍五入前的字符串
	 * @param cont
	 *            小数点后保留的位数
	 * @return 四舍五入以后 的字符串
	 */
	public static String round(String strNumber, int cont) {
		int mi = 1;
		for (int i = 0; i <= cont; i++) {
			mi = mi * 10;
		}
		if (strNumber == null) {
			strNumber = "0";
		}
		double d = new Double(strNumber).doubleValue();
		BigDecimal number = new BigDecimal(d * mi);
		BigInteger integer = new BigInteger("0");
		int end = Integer.parseInt(number.toBigInteger().toString().substring(
				number.toBigInteger().toString().length() - 1));
		if (end >= 5) {
			if (d > 0) {
				integer = number.toBigInteger().add(new BigInteger("10"));
			} else {
				integer = number.toBigInteger().subtract(new BigInteger("10"));
			}
		} else {
			integer = number.toBigInteger();
		}
		integer = integer.divide(new BigInteger("10"));
		number = new BigDecimal(integer, cont);
		return number.toString();
	}

	/**
	 * 对Hashtable排序 wjz 0830
	 * 
	 * @param ht
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static Hashtable sortHashTable(Hashtable ht) {
		if (ht == null || ht.size() == 0) {
			return ht;
		}
		try {

			// ht.put("03", "外资年报");
			// ht.put("01", "增值税一般人");
			// ht.put("04", "外资季报");
			// ht.put("02", "增值税小规模");
			Vector v = new Vector(ht.keySet());
			// zxt.pub.util.LogUtil.getLogger().debug(v);
			Collections.sort(v);
			Object[] keys = v.toArray();
			int len = ht.size();
			Hashtable ht2 = new Hashtable(len);
			for (int i = 0; i < len; i++) {

				ht2.put(keys[i], ht.get(keys[i]));
			}
			return ht2;
		} catch (Exception ex) {
			LogUtil.getLogger().error("sort error", ex);
			return ht;
		}

	}

	/**
	 * 取子数组
	 * 
	 * @param source
	 *            Description of Parameter
	 * @param from
	 *            Description of Parameter
	 * @param end
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static byte[] subBytes(byte source[], int from, int end) {
		if (from > end || end > source.length - 1) {
			return null;
		}
		byte btRet[] = new byte[(end - from) + 1];
		for (int i = 0; i <= end - from; i++) {
			btRet[i] = source[i + from];
		}

		return btRet;
	}

	/**
	 * Description of the Method
	 * 
	 * @param foo
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static byte[] toByteArray(short foo) {
		return toByteArray(foo, new byte[2]);
	}

	/**
	 * Description of the Method
	 * 
	 * @param foo
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static byte[] toByteArray(int foo) {
		return toByteArray(foo, new byte[4]);
	}

	/**
	 * Description of the Method
	 * 
	 * @param foo
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static byte[] toByteArray(long foo) {
		return toByteArray(foo, new byte[8]);
	}

	/**
	 * Description of the Method
	 * 
	 * @param foo
	 *            Description of Parameter
	 * @param array
	 *            Description of Parameter
	 * @return Description of the Return Value
	 * @eturn Description of the Returned Value
	 */
	private static byte[] toByteArray(long foo, byte[] array) {
		int len = array.length;
		for (int iInd = 0; iInd < len; iInd++) {
			array[iInd] = (byte) ((foo >> ((len - iInd - 1) * 8)) & 0x000000FF);
		}
		return array;
	}

	/**
	 * 将字符串转换为中文
	 * 
	 * @param src
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static String toCHNString(String src) {
		if (src == null || src.length() <= 0) {
			return src;
		}
		try {
			return new String(src.getBytes("ISO-8859-1"), "GB2312");
		} catch (java.io.UnsupportedEncodingException ex) {
			LogUtil.getLogger().error(ex.getMessage(), ex);
			return null;
		}

	}

	/**
	 * ADD BY YQL2005-07-01 将字符串转换为中文ISO-8859-1字符集
	 * 
	 * @param src
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static String GBKtoISOString(String src) {
		if (src == null || src.length() <= 0) {
			return src;
		}
		try {
			return new String(src.getBytes("GBK"), "ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException ex) {
			LogUtil.getLogger().error(ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * ADD BY YQL2005-07-01 将字符串转换为中文ISO-8859-1字符集
	 * 
	 * @param src
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static String GB2312toISOString(String src) {
		if (src == null || src.length() <= 0) {
			return src;
		}
		try {
			return new String(src.getBytes("GB2312"), "ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException ex) {
			LogUtil.getLogger().error(ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * 将字符串转换为中文GBK字符集
	 * 
	 * @param src
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static String toGBKString(String src) {
		if (src == null || src.length() <= 0) {
			return src;
		}
		try {
			return new String(src.getBytes("ISO-8859-1"), "GBK");
		} catch (java.io.UnsupportedEncodingException ex) {
			LogUtil.getLogger().error(ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * 将对象转换为浮点数 add by wjz 1119
	 * 
	 * @param o
	 *            源对象
	 * @param forceConvert
	 *            是不是强制转换
	 * @return 对应的浮点数
	 */
	public static double toDouble(Object o, boolean forceConvert) {

		try {
			String s = o.toString();
			return Double.parseDouble(s);
		} catch (Exception ex) {

			if (forceConvert) {
				return 0;
			}
			throw new IllegalArgumentException("该对象为空或不是一个合法的浮点数");
		}

	}

	/**
	 * Description of the Method
	 * 
	 * @param src
	 *            Description of Parameter
	 * @return Description of the Returned Value
	 */
	public static String toHexsFormat(byte[] src) {
		if (src == null) {
			return null;
		}
		int len = src.length;
		StringBuffer sb = new StringBuffer();

		String s = "";
		sb.append(",value=");

		for (int i = 0; i < len; i++) {
			s = java.lang.Integer.toHexString(src[i] & 0XFF);
			if (s.length() == 1) {
				sb.append("0");
			}
			sb.append(s);
			sb.append(",");
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 根据四个字节的物理存储,生成一个整数
	 * <p>
	 * 例如e0,08,00,00生成整数2272
	 * <p>
	 * wujinzhong 0610
	 * 
	 * @param bts
	 *            Description of Parameter
	 * @return The Int value
	 */
	public static int toInt(byte[] bts) {
		if (bts == null) {
			return Integer.MIN_VALUE;
		}
		int v = 0;
		int len = bts.length;
		for (int i = len - 1; i >= 0; i--) {
			v = v * 256 + ((bts[i] + 256) % 256);

		}
		return v;
	}

	/**
	 * 将对象转换为整数型
	 * 
	 * @param o
	 *            源对象
	 * @return 对应的Int值,如果出错,则返回Integer.MIN_VALUE
	 */
	public static int toInt(Object o) {
		if (o == null) {
			throw new IllegalArgumentException("该对象为空");
		}
		String s = o.toString();
		try {
			return Integer.parseInt(s);
		} catch (Exception ex) {

			LogUtil.getLogger().error(ex.getMessage(), ex);
			return Integer.MIN_VALUE;
		}
	}

	/**
	 * 将对象转换为整数型
	 * 
	 * @param o
	 *            源对象
	 * @param defaultInt
	 * @return 对应的Int值,如果出错,则返回defaultInt
	 */
	public static int toInt(Object o, int defaultInt) {
		try {
			if (o instanceof BigDecimal) {
				return ((BigDecimal) o).intValue();
			} else if (o instanceof String) {
				return new BigDecimal((String) o).setScale(0,
						BigDecimal.ROUND_HALF_UP).intValue();
			}
			return Integer.parseInt(o.toString());
		} catch (Exception ex) {
			return defaultInt;
		}
	}

	/**
	 * 将对象转换为整数型
	 * 
	 * @param o
	 *            源对象
	 * @return 对应的Long值,如果出错,则返回Long.MIN_VALUE
	 */
	public static long toLong(Object o) {
		if (o == null) {
			throw new IllegalArgumentException("该对象为空");
		}
		String s = o.toString();
		try {
			return Long.parseLong(s);
		} catch (Exception ex) {

			LogUtil.getLogger().error(ex.getMessage(), ex);
			return Long.MAX_VALUE;
		}
	}
	
	
	/**
	 * 判断字符串是否为数字
	 */
	static Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
	public static boolean isDecimal(String str) {
		if(isEmptyStr(str)) return false;  
		return pattern.matcher(str).matches();
	}
	

	/**
	 * 将对象转换为Long整型
	 * 
	 * @param o
	 *            源对象
	 * @param defaultLong
	 * @return 对应的long值,如果出错,则返回defaultLong
	 */
	public static long toLong(Object o, long defaultLong) {
		try {
			if (o instanceof BigDecimal) {
				return ((BigDecimal) o).longValue();
			}
			return Long.parseLong(o.toString());
		} catch (Exception ex) {
			return defaultLong;
		}
	}

	/**
	 * 将对象转换为BigDecimal数型
	 * 
	 * @param o
	 *            源对象
	 * @return 对应的整数值,如果出错,则返回Long.MIN_VALUE
	 */
	public static BigDecimal toBigDecimal(Object o) {
		if (o == null) {
			throw new IllegalArgumentException("该对象为空");
		}
		String s = o.toString();
		try {
			return new BigDecimal(s);
		} catch (Exception ex) {

			LogUtil.getLogger().error(ex.getMessage(), ex);
			return new BigDecimal(Long.MAX_VALUE);
		}
	}

	/**
	 * 将对象转换为BigDecimal整型
	 * 
	 * @param o
	 *            源对象
	 * @param defaultBigDecimal
	 * @return 对应的BigDecimal值,如果出错,则返回defaultBigDecimal
	 */
	public static BigDecimal toBigDecimal(Object o, BigDecimal defaultBigDecimal) {
		try {
			return new BigDecimal(o.toString());
		} catch (Exception ex) {
			return defaultBigDecimal;
		}
	}

	/**
	 * 从Vector转换为字符串数组的形式 add by yql 1129
	 * 
	 * @param vec
	 * @return
	 */
	public static String[] vecToStrArr(Vector vec) {
		if (vec == null) {
			return null;
		}
		int iLen = vec.size();
		String[] rtnArr = new String[iLen];
		for (int i = 0; i < iLen; i++) {
			rtnArr[i] = vec.elementAt(i).toString();
		}
		return rtnArr;
	}

	/**
	 * 返回对象的字符串表现，如果为空则返回"" add by wjz 040902
	 * 
	 * @param obj
	 *            Object
	 * @return String
	 */
	public static String toNotNullString(Object obj) {
		if (obj == null)
			return "";
		return obj.toString();
	}

	public static String quoteStr(Object o) {
		return "'" + o + "'";
	}

	/**
	 * 将BCD码还原为字符串，目前不支持汉字
	 * <p>
	 * add by wjz 040916
	 * 
	 * @param bt
	 *            byte[] BCD格式的数组
	 * @return String
	 */
	public static String BCDToStr(byte[] bt) {
		String s = "";
		if (bt == null)
			return "";
		byte[] bt2 = new byte[bt.length * 2];
		byte b = 0;
		byte b1 = 0;
		byte b2 = 0;
		char c = 0;
		try {
			int len = bt2.length;
			int pos = 0;
			for (int i = 0; i < bt.length; i++) {
				b = bt[i];
				bt2[i * 2] = (byte) (bt[i] >> 4 & 0x0f); // 高位
				bt2[i * 2 + 1] = (byte) (bt[i] & 0x0f); // 低位

			}
			boolean flag = true; // 一个字符的开始
			for (int i = 0; i < len; i++) {
				b = bt2[i];
				if (flag) {
					if (b <= 9) {
						s = s + (char) (b + 0x30);
					} else {
						flag = false;
					}
				} else { // 不是开始，与前面合并
					if (b == 0x0f && (i == len - 1))
						continue;

					s = s + (char) ((bt2[i - 1] << 4) + b - 128);

					flag = true;

				}
			}
			return s;
		} catch (Exception ex) {
			LogUtil.getLogger().error(
					"BCDToStr ERROR:" + ex.getMessage(), ex);
			return null;
		}

	}

	/**
	 * 将字符串转换为BCD码，目前不支持汉字
	 * <p>
	 * add by wjz 040916
	 * 
	 * @param s
	 *            String
	 * @return byte[] BCD格式的数组
	 */
	public static byte[] strToBCD(String s) {
		byte[] bt = new byte[s.length()];
		// 初始化
		for (int i = 0; i < bt.length; i++) {
			bt[i] = (byte) 0xff;
		}
		char[] chars = s.toCharArray();
		byte c = 0;
		try {
			int len = chars.length;
			int pos = 0;
			boolean flag = true; // true-high position;false-lower position
			for (int i = 0; i < len; i++) {
				c = (byte) (chars[i]);
				if (c >= '0' && c <= '9') { // 数字
					if (!flag) { // 低字节
						bt[pos] = (byte) (bt[pos] + (c - 0x30));
						flag = true;
						pos++;
					} else { // 高字节
						bt[pos] = (byte) ((c - 0x30) << 4);
						flag = false;
					}
				} else { // 字符
					if (!flag) { // 低字节
						bt[pos] = (byte) ((byte) (bt[pos]) + (byte) ((c + 128) >> 4));
						pos++;
						bt[pos] = (byte) (c << 4);
					} else { // 高字节
						bt[pos] = (byte) (c + 128);
						pos++;
					}

				}

			}
			if (!flag) {
				bt[pos] = (byte) (bt[pos] + 0x0f);
				pos++;
			}
			byte[] ret = new byte[pos];
			System.arraycopy(bt, 0, ret, 0, pos);
			return ret;
		} catch (Exception ex) {
			LogUtil.getLogger().error(
					"strToBCD ERROR:" + ex.getMessage(), ex);
			return null;
		}

	}

	public static Integer parseInteger(Object o, Integer defaulti) {
		try {
			return new Integer(Integer.parseInt(o.toString()));
		} catch (Exception e) {
			return defaulti;
		}
	}

	public static Double parseDouble(Object o, Double defaulti) {
		try {
			return new Double(Double.parseDouble(o.toString()));
		} catch (Exception e) {
			return defaulti;
		}
	}

	public static Float parseFloat(Object o, Float defaulti) {
		try {
			return new Float(Float.parseFloat(o.toString()));
		} catch (Exception e) {
			return defaulti;
		}
	}

	public static Long parseLong(Object o, Long defaulti) {
		try {
			return new Long(Long.parseLong(o.toString()));
		} catch (Exception e) {
			return defaulti;
		}
	}

	public static BigDecimal parseBigDecimal(Object o, BigDecimal defaulti) {
		try {
			return new BigDecimal(o.toString());
		} catch (Exception e) {
			return defaulti;
		}
	}

	public static Date parseDate(Object o, Date defaulti) {
		if (o instanceof Date) {
			return (Date) o;
		}
		try {
			return DateUtil.strToDate(o.toString());
		} catch (Exception e) {
			return defaulti;
		}
	}

	/**
	 * "1001,1002"-->"'1001','1002'"
	 * 
	 * @param s
	 * @return
	 */
	public static String getInSQLStr(String s) {
		if (s == null || s.length() <= 0)
			return "";
		String[] ss = s.split(",");
		return getInSQLStr(ss);
	}

	public static String getInSQLStr(Object[] ss) {
		if (ss == null)
			return "";
		String retstr = "";
		for (int i = 0; i < ss.length; i++) {
			if (i != 0)
				retstr += ",";
			retstr += "'" + ss[i] + "'";
		}
		return retstr;
	}

	public synchronized static String getSysId() {
		try {
			Thread.sleep(36);
		} catch (Exception e) {

		}
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
		Date d = getSysDateTime();
		return sf.format(d);
	}

	/**
	 * 取系统时间，包含时、分、秒
	 * 
	 * @return
	 */
	public static Date getSysDateTime() {
		return new Date();
	}
	
	
	public static BigDecimal decimalDivide(BigDecimal cs, BigDecimal bcs, int scale) {
		return cs.divide(bcs ,scale, BigDecimal.ROUND_HALF_UP);
	}

}