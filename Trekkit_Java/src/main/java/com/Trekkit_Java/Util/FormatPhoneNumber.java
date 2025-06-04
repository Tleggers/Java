package com.Trekkit_Java.Util;

public class FormatPhoneNumber {
	
	// 휴대폰 번호 정규화
	public static String formatPhoneNumber(String phone) {
		
	    if (phone == null || phone.length() != 11) return phone; // 예외처리 

	    return phone.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
	    
	}

}
