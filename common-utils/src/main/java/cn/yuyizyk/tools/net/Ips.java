package cn.yuyizyk.tools.net;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.apache.commons.lang3.StringUtils;

import cn.yuyizyk.tools.lang.Regs;

public class Ips {

	/**
	 * @param ip
	 * @return byte[4]
	 */
	public static byte[] getIpByteArrayFromString(String ip) {
		byte[] ret = new byte[4];
		java.util.StringTokenizer st = new java.util.StringTokenizer(ip, ".");
		try {
			ret[0] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[1] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[2] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
			ret[3] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return ret;
	}

	public static void main(String args[]) {
		byte[] a = getIpByteArrayFromString(args[0]);
		for (int i = 0; i < a.length; i++)
			System.out.println(a[i]);
		System.out.println(getIpStringFromBytes(a));
	}

	/**
	 * 对原始字符串进行编码转换，如果失败，返回原始的字符串
	 *
	 * @param s
	 *            原始字符串
	 * @param srcEncoding
	 *            源编码方式
	 * @param destEncoding
	 *            目标编码方式
	 * @return 转换编码后的字符串，失败返回原始字符串
	 */
	public static String getString(String s, String srcEncoding, String destEncoding) {
		try {
			return new String(s.getBytes(srcEncoding), destEncoding);
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}

	/**
	 * 根据某种编码方式将字节数组转换成字符串
	 *
	 * @param b
	 *            字节数组
	 * @param encoding
	 *            编码方式
	 * @return 如果encoding不支持，返回一个缺省编码的字符串
	 */
	public static String getString(byte[] b, String encoding) {
		try {
			return new String(b, encoding);
		} catch (UnsupportedEncodingException e) {
			return new String(b);
		}
	}

	/**
	 * 根据某种编码方式将字节数组转换成字符串
	 *
	 * @param b
	 *            字节数组
	 * @param offset
	 *            要转换的起始位置
	 * @param len
	 *            要转换的长度
	 * @param encoding
	 *            编码方式
	 * @return 如果encoding不支持，返回一个缺省编码的字符串
	 */
	public static String getString(byte[] b, int offset, int len, String encoding) {
		try {
			return new String(b, offset, len, encoding);
		} catch (UnsupportedEncodingException e) {
			return new String(b, offset, len);
		}
	}

	/**
	 * @param ip
	 *            ip的字节数组形式
	 * @return 字符串形式的ip
	 */
	public static String getIpStringFromBytes(byte[] ip) {
		StringBuffer sb = new StringBuffer();
		sb.append(ip[0] & 0xFF);
		sb.append('.');
		sb.append(ip[1] & 0xFF);
		sb.append('.');
		sb.append(ip[2] & 0xFF);
		sb.append('.');
		sb.append(ip[3] & 0xFF);
		return sb.toString();
	}

	public static byte[] ip2Bytes(String ip) {
		// Make a first pass to categorize the characters in this string.
		boolean hasColon = false;
		boolean hasDot = false;
		for (int i = 0; i < ip.length(); i++) {
			char c = ip.charAt(i);
			if (c == '.') {
				hasDot = true;
			} else if (c == ':') {
				if (hasDot) {
					return null; // Colons must not appear after dots.
				}
				hasColon = true;
			} else if (Character.digit(c, 16) == -1) {
				return null; // Everything else must be a decimal or hex digit.
			}
		}

		// Now decide which address family to parse.
		if (hasColon) {
			if (hasDot) {
				ip = convertDottedQuadToHex(ip);
				if (ip == null) {
					return null;
				}
			}
			return textToNumericFormatV6(ip);
		} else if (hasDot) {
			return textToNumericFormatV4(ip);
		}
		return null;
	}

	private static String convertDottedQuadToHex(String ipString) {
		int lastColon = ipString.lastIndexOf(':');
		String initialPart = ipString.substring(0, lastColon + 1);
		String dottedQuad = ipString.substring(lastColon + 1);
		byte[] quad = textToNumericFormatV4(dottedQuad);
		if (quad == null) {
			return null;
		}
		String penultimate = Integer.toHexString(((quad[0] & 0xff) << 8) | (quad[1] & 0xff));
		String ultimate = Integer.toHexString(((quad[2] & 0xff) << 8) | (quad[3] & 0xff));
		return initialPart + penultimate + ":" + ultimate;
	}

	private static byte[] textToNumericFormatV4(String ipString) {
		String[] address = ipString.split("\\.", IPV4_PART_COUNT + 1);
		if (address.length != IPV4_PART_COUNT) {
			return null;
		}

		byte[] bytes = new byte[IPV4_PART_COUNT];
		try {
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = parseOctet(address[i]);
			}
		} catch (NumberFormatException ex) {
			return null;
		}

		return bytes;
	}

	private static byte[] textToNumericFormatV6(String ipString) {
		String[] parts = ipString.split(":", IPV6_PART_COUNT + 2);
		if (parts.length < 3 || parts.length > IPV6_PART_COUNT + 1) {
			return null;
		}

		int skipIndex = -1;
		for (int i = 1; i < parts.length - 1; i++) {
			if (parts[i].length() == 0) {
				if (skipIndex >= 0) {
					return null;
				}
				skipIndex = i;
			}
		}

		int partsHi;
		int partsLo;
		if (skipIndex >= 0) {
			partsHi = skipIndex;
			partsLo = parts.length - skipIndex - 1;
			if (parts[0].length() == 0 && --partsHi != 0) {
				return null;
			}
			if (parts[parts.length - 1].length() == 0 && --partsLo != 0) {
				return null;
			}
		} else {
			partsHi = parts.length;
			partsLo = 0;
		}

		int partsSkipped = IPV6_PART_COUNT - (partsHi + partsLo);
		if (!(skipIndex >= 0 ? partsSkipped >= 1 : partsSkipped == 0)) {
			return null;
		}

		ByteBuffer rawBytes = ByteBuffer.allocate(2 * IPV6_PART_COUNT);
		try {
			for (int i = 0; i < partsHi; i++) {
				rawBytes.putShort(parseHextet(parts[i]));
			}
			for (int i = 0; i < partsSkipped; i++) {
				rawBytes.putShort((short) 0);
			}
			for (int i = partsLo; i > 0; i--) {
				rawBytes.putShort(parseHextet(parts[parts.length - i]));
			}
		} catch (NumberFormatException ex) {
			return null;
		}
		return rawBytes.array();
	}

	private static short parseHextet(String ipPart) {
		int hextet = Integer.parseInt(ipPart, 16);
		if (hextet > 0xffff) {
			throw new NumberFormatException();
		}
		return (short) hextet;
	}

	private static byte parseOctet(String ipPart) {
		int octet = Integer.parseInt(ipPart);
		if (octet > 255 || (ipPart.startsWith("0") && ipPart.length() > 1)) {
			throw new NumberFormatException();
		}
		return (byte) octet;
	}

	private static final int IPV4_PART_COUNT = 4;
	private static final int IPV6_PART_COUNT = 8;

	public static int bytesCompare(byte[] bytes1, byte[] bytes2) {
		if (bytes1 == bytes2) {
			return 0;
		}
		int len1 = bytes1.length;
		int len2 = bytes2.length;
		int len = len1 < len2 ? len1 : len2;
		for (int i = 0; i < len; i++) {
			int a = (bytes1[i] & 0xff);
			int b = (bytes2[i] & 0xff);
			if (a != b) {
				return a - b;
			}
		}
		return 0;
	}

	public static boolean inRange(String range, String ip) {
		if (StringUtils.isBlank(range)) {
			return true;
		}
		byte[] ipBytes = ip2Bytes(ip);
		if (ipBytes == null) {
			return false;
		}
		range = StringUtils.remove(range, ' ');
		range = StringUtils.remove(range, '\r');
		int index;
		byte[] lineBytes, beginBytes, endBytes;
		for (String line : StringUtils.split(range, '\n')) {
			if (StringUtils.isNotBlank(line)) {
				index = line.indexOf('-');
				if (index != -1) {
					beginBytes = ip2Bytes(line.substring(0, index));
					endBytes = ip2Bytes(line.substring(index + 1));
					if (beginBytes == null || endBytes == null || beginBytes.length != ipBytes.length
							|| endBytes.length != ipBytes.length) {
						continue;
					}
					if (bytesCompare(beginBytes, ipBytes) <= 0 && bytesCompare(endBytes, ipBytes) >= 0) {
						return true;
					}
				} else {
					lineBytes = ip2Bytes(line);
					if (lineBytes == null || lineBytes.length != ipBytes.length) {
						continue;
					}
					if (bytesCompare(lineBytes, ipBytes) == 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 根据long值获取ip v4地址
	 * 
	 * @param longIP
	 *            IP的long表示形式
	 * @return IP V4 地址
	 */
	public static String longToIpv4(long longIP) {
		final StringBuilder sb = new StringBuilder();
		// 直接右移24位
		sb.append(String.valueOf(longIP >>> 24));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf(longIP & 0x000000FF));
		return sb.toString();
	}

	/**
	 * 根据ip地址计算出long型的数据
	 * 
	 * @param strIP
	 *            IP V4 地址
	 * @return long值
	 */
	public static long ipv4ToLong(String strIP) {
		if (Regs.isMatcher(strIP, Regs.V_IP4)) {
			long[] ip = new long[4];
			// 先找到IP地址字符串中.的位置
			int position1 = strIP.indexOf(".");
			int position2 = strIP.indexOf(".", position1 + 1);
			int position3 = strIP.indexOf(".", position2 + 1);
			// 将每个.之间的字符串转换成整型
			ip[0] = Long.parseLong(strIP.substring(0, position1));
			ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
			ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
			ip[3] = Long.parseLong(strIP.substring(position3 + 1));
			return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
		}
		return 0;
	}

	public static final String IPV4_A_BEGIN = "10.0.0.0";
	public static final String IPV4_A_END = "10.255.255.255";
	public static final String IPV4_B_BEGIN = "172.16.0.0";
	public static final String IPV4_B_END = "172.31.255.255";
	public static final String IPV4_C_BEGIN = "192.168.0.0";
	public static final String IPV4_C_END = "192.168.255.255";

	/**
	 * 判定是否为内网IP<br>
	 * 私有IP：A类 10.0.0.0-10.255.255.255 B类 172.16.0.0-172.31.255.255 C类
	 * 192.168.0.0-192.168.255.255 当然，还有127这个网段是环回地址
	 * 
	 * @param ipAddress
	 *            IP地址
	 * @return 是否为内网IP
	 */
	public static boolean isInnerIP(String ipAddress) {
		boolean isInnerIp = false;
		long ipNum = ipv4ToLong(ipAddress);

		long aBegin = ipv4ToLong(IPV4_A_BEGIN);
		long aEnd = ipv4ToLong(IPV4_A_END);

		long bBegin = ipv4ToLong(IPV4_B_BEGIN);
		long bEnd = ipv4ToLong(IPV4_B_END);

		long cBegin = ipv4ToLong(IPV4_C_BEGIN);
		long cEnd = ipv4ToLong(IPV4_C_END);

		isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd)
				|| ipAddress.equals(Nets.LOCAL_IP);
		return isInnerIp;
	}

	/**
	 * 隐藏掉IP地址的最后一部分为 * 代替
	 * 
	 * @param ip
	 *            IP地址
	 * @return 隐藏部分后的IP
	 */
	public static String hideIpPart(String ip) {
		return new StringBuffer(ip.length()).append(ip.substring(0, ip.lastIndexOf(".") + 1)).append("*").toString();
	}

	/**
	 * 创建 {@link InetSocketAddress}
	 * 
	 * @param host
	 *            域名或IP地址
	 * @param port
	 *            端口
	 * @return {@link InetSocketAddress}
	 */
	public static InetSocketAddress createAddress(String host, int port) {
		return new InetSocketAddress(host, port);
	}

	/// ------------------------------------------------------------

	/**
	 * 指定IP的long是否在指定范围内
	 * 
	 * @param userIp
	 *            用户IP
	 * @param begin
	 *            开始IP
	 * @param end
	 *            结束IP
	 * @return 是否在范围内
	 */
	private static boolean isInner(long userIp, long begin, long end) {
		return (userIp >= begin) && (userIp <= end);
	}
}
