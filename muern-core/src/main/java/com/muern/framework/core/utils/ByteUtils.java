package com.muern.framework.core.utils;

import java.nio.charset.StandardCharsets;

import static com.muern.framework.core.common.Constant.N2;

/**
 * @author gegeza
 */
public final class ByteUtils {

	/**
	 * 将byte数组转换为表示16进制值的字符串，如：byte[]{8,18}转换为：0813
	 * @param src 需要转换的byte数组
	 * @return 转换后的十六进制字符串
	 */
	public static String byteToHex(byte[] src) {
		int iLen = src.length;
		// 每个byte(8位)用两个(16进制)字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuilder sb = new StringBuilder(iLen * 2);
		for (int tmp : src) {
			// 把负数转换为正数
			while (tmp < 0) {
				tmp = tmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (tmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(tmp, 16));
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * 将byte数组转为字符串
	 * @param src 需要转换的byte数组
	 * @return 转换后的字符串
	 */
	public static String byteToStr(byte[] src) {
		return new String(src, StandardCharsets.UTF_8);
	}

	/**
	 * 将表示16进制值的字符串转换为byte数组
	 * @param src 需要转换的字符串
	 * @return 转换后的byte数组
	 */
	public static byte[] hexToByte(String src) {
		byte[] srcBytes = src.getBytes();
		int iLen = srcBytes.length;
		// 两个(16进制)字符表示一个字节(8位)，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + N2) {
			String strTmp = new String(srcBytes, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	/**
	 * 将两个byte数组相加组成一个新的byte数组
	 * @param a 原byte数组a
	 * @param b 原byte数组b
	 * @return 相加后的新byte数组
	 */
	public static byte[] byteAdd(byte[] a, byte[] b){
		if (a == null) {
			return b == null ? null : b.clone();
		} else if (b == null) {
			return a.clone();
		} else {
			byte[] ret = new byte[a.length + b.length];
			System.arraycopy(a, 0, ret, 0, a.length);
			System.arraycopy(b, 0, ret, a.length, b.length);
			return ret;
		}
	}
}
