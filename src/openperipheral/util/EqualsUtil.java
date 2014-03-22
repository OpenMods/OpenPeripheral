package openperipheral.util;

public class EqualsUtil {
	public static boolean equals(Object a, Object b){
		if(a==null && b==null){
			return true;
		}else if(a!=null && b!=null){
			return a.equals(b);
		}else{
			return false; // At least one of them is null. 
		}
	}
}
