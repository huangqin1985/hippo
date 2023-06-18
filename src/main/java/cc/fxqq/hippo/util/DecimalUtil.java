package cc.fxqq.hippo.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.google.common.collect.Lists;

public class DecimalUtil {
	
	private static DecimalFormat percentPattern = new DecimalFormat("0.##%");
	private static DecimalFormat pattern = new DecimalFormat("0.##");
	private static DecimalFormat pattern2 = new DecimalFormat("0.00");
	
	private static DecimalFormat digits3 = new DecimalFormat("0.###");
	
	public static String format(BigDecimal b) {
		return pattern.format(b);
	}
	
	public static String formatFull(BigDecimal b) {
		return pattern2.format(b);
	}
	
	public static String format3Digit(BigDecimal b) {
		return digits3.format(b);
	}

	public static BigDecimal max(BigDecimal... arg) {
		if (arg.length == 0) {
			throw new IllegalArgumentException("max");
		}
		
		return Lists.newArrayList(arg).stream().max(BigDecimal::compareTo).get();

	}
	
	public static BigDecimal min(BigDecimal... arg) {
		if (arg.length == 0) {
			throw new IllegalArgumentException("min");
		}
		
		return Lists.newArrayList(arg).stream().min(BigDecimal::compareTo).get();

	}
	
	public static BigDecimal get(double d) {
		BigDecimal bd = new BigDecimal(d);
		
		return bd.setScale(2, RoundingMode.HALF_DOWN);
	}
	
	public static BigDecimal get(BigDecimal bd) {
		if (bd == null) {
			return BigDecimal.ZERO; 
		} else {
			return bd.setScale(2, RoundingMode.HALF_DOWN);
		}
	}
	
	public static BigDecimal get(BigDecimal bd, Integer newScale) {
		if (bd == null) {
			return BigDecimal.ZERO; 
		} else {
			return bd.setScale(newScale, RoundingMode.HALF_DOWN);
		}
	}
	
	public static BigDecimal add(BigDecimal... bds) {
		BigDecimal result = BigDecimal.ZERO;
		
		for (BigDecimal bd : bds) {
			result = result.add(get(bd));
		}
		return result.setScale(2, RoundingMode.HALF_DOWN);
	}
	
	public static BigDecimal subtract(BigDecimal first, BigDecimal... bds) {
		BigDecimal result = first;
		
		for (BigDecimal bd : bds) {
			result = result.subtract(get(bd));
		}
		return result.setScale(2, RoundingMode.HALF_DOWN);
	}

	public static String getPercentStr(BigDecimal rate) {
		if (rate == null) { 
			return "-";
		}
		
		return percentPattern.format(rate);
	}
	
	public static String getPercentStr(BigDecimal first, BigDecimal second) {
		if (BigDecimal.ZERO.compareTo(second) == 0) { 
			return "-";
		}
		
		BigDecimal percentDec = first.divide(second, 4, RoundingMode.HALF_UP);
		
		return percentPattern.format(percentDec);
	}
	
	public static BigDecimal getPercent(BigDecimal first, BigDecimal second) {
		if (BigDecimal.ZERO.compareTo(second) == 0) { 
			return null;
		}
		
		BigDecimal percentDec = first.divide(second, 4, RoundingMode.HALF_UP);
		
		return percentDec;
	}
	
	public static BigDecimal getPercent2(BigDecimal first, BigDecimal second) {
		if (BigDecimal.ZERO.compareTo(second) == 0) { 
			return null;
		}
		
		BigDecimal percentDec = first.divide(second, 4, RoundingMode.HALF_UP)
				.multiply(new BigDecimal(100));
		
		return percentDec;
	}
	
	public static String removePecimalPoint(BigDecimal dec) {
		String decStr = String.valueOf(dec);
		int idx = decStr.lastIndexOf('.');
		if (idx == -1) {
			return Integer.toString(dec.intValue());
		}
		int pointIdx = decStr.length() - decStr.lastIndexOf('.') - 1;
		int result = dec.multiply(new BigDecimal(Math.pow(10 ,pointIdx))).intValue();
		
		return Integer.toString(result);
		
	}
}
