package openperipheral.core.util;

import java.util.Random;

public class StringUtils {

	public static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static Random rnd = new Random();

	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	public static String join(String r[], String d) {
		if (r.length == 0) return "";
		StringBuilder sb = new StringBuilder();
		int i;
		for (i = 0; i < r.length - 1; i++)
			sb.append(r[i] + d);
		return sb.toString() + r[i];
	}
	
	public static boolean isEmpty(String str){
	  return str == null || "".equals(str);
	}
}
