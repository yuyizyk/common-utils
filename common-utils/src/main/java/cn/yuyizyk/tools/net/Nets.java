package cn.yuyizyk.tools.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;

import cn.yuyizyk.tools.common.Assert;
import cn.yuyizyk.tools.exception.Exceptions;
import cn.yuyizyk.tools.lang.Arrs;
import cn.yuyizyk.tools.lang.Strs;

public class Nets {
	public final static String LOCAL_IP = "127.0.0.1";

	/**
	 * 是否为有效的端口
	 * 
	 * @param port
	 *            端口号
	 * @return 是否有效
	 */
	public static boolean isValidPort(int port) {
		// 有效端口是0～65535
		return port >= 0 && port <= 0xFFFF;
	}

	/**
	 * 检测本地端口可用性
	 * 
	 * @param port
	 *            被检测的端口
	 * @return 是否可用
	 */
	public static boolean isUsableLocalPort(int port) {
		if (false == isValidPort(port)) {
			// 给定的IP未在指定端口范围中
			return false;
		}
		try {
			new Socket(LOCAL_IP, port).close();
			// socket链接正常，说明这个端口正在使用
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	/**
	 * 相对URL转换为绝对URL
	 * 
	 * @param absoluteBasePath
	 *            基准路径，绝对
	 * @param relativePath
	 *            相对路径
	 * @return 绝对URL
	 */
	public static String toAbsoluteUrl(String absoluteBasePath, String relativePath) {
		try {
			URL absoluteUrl = new URL(absoluteBasePath);
			return new URL(absoluteUrl, relativePath).toString();
		} catch (Exception e) {
			throw Exceptions.box(e,
					Strs.format("To absolute url [{}] base [{}] error!", relativePath, absoluteBasePath));
		}
	}

	/**
	 * 
	 * 使用普通Socket发送数据
	 * 
	 * @param host
	 *            Server主机
	 * @param port
	 *            Server端口
	 * @param data
	 *            数据
	 * @throws IOException
	 *             IO异常
	 * @since 3.3.0
	 */
	public static void netCat(String host, int port, byte[] data) {
		try (Socket socket = new Socket(host, port); OutputStream out = socket.getOutputStream();) {
			out.write(data);
			out.flush();
		} catch (IOException e) {
			throw Exceptions.box(e, true);
		}
	}

	/**
	 * 
	 * 简易的使用Socket发送数据
	 * 
	 * @param host
	 *            Server主机
	 * @param port
	 *            Server端口
	 * @param isBlock
	 *            是否阻塞方式
	 * @param data
	 *            需要发送的数据
	 * @throws IORuntimeException
	 *             IO异常
	 * @since 3.3.0
	 */
	public static void netCat(String host, int port, boolean isBlock, ByteBuffer data) {
		try (SocketChannel channel = SocketChannel.open(Ips.createAddress(host, port))) {
			channel.configureBlocking(isBlock);
			channel.write(data);
		} catch (IOException e) {
			throw Exceptions.box(e, true);
		}
	}

	/**
	 * 获得指定地址信息中的MAC地址
	 * 
	 * @param inetAddress
	 *            {@link InetAddress}
	 * @param separator
	 *            分隔符，推荐使用“-”或者“:”
	 * @return MAC地址，用-分隔
	 */
	public static String getMacAddress(InetAddress inetAddress, String separator) {
		if (null == inetAddress) {
			return null;
		}

		byte[] mac;
		try {
			mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
		} catch (SocketException e) {
			throw Exceptions.box(e);
		}
		if (null != mac) {
			final StringBuilder sb = new StringBuilder();
			String s;
			for (int i = 0; i < mac.length; i++) {
				if (i != 0) {
					sb.append(separator);
				}
				// 字节转换为整数
				s = Integer.toHexString(mac[i] & 0xFF);
				sb.append(s.length() == 1 ? 0 + s : s);
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * 获取本机所有网卡
	 * 
	 * @return 所有网卡，异常返回<code>null</code>
	 */
	public static Collection<NetworkInterface> getNetworkInterfaces() {
		Enumeration<NetworkInterface> networkInterfaces = null;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			return null;
		}
		Collection<NetworkInterface> c = new ArrayList<NetworkInterface>();
		Arrs.asIterator(networkInterfaces).forEachRemaining(c::add);
		return c;
	}

	/**
	 * 获得本机的IP地址列表<br>
	 * 返回的IP列表有序，按照系统设备顺序
	 * 
	 * @return IP地址列表 {@link LinkedHashSet}
	 */
	public static LinkedHashSet<String> localIpv4s() {
		Enumeration<NetworkInterface> networkInterfaces = null;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			throw Exceptions.box(e, true);
		}

		Assert.isNull(networkInterfaces, "Get network interface error!");

		final LinkedHashSet<String> ipSet = new LinkedHashSet<>();

		while (networkInterfaces.hasMoreElements()) {
			final NetworkInterface networkInterface = networkInterfaces.nextElement();
			final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
			while (inetAddresses.hasMoreElements()) {
				final InetAddress inetAddress = inetAddresses.nextElement();
				if (inetAddress != null && inetAddress instanceof Inet4Address) {
					ipSet.add(inetAddress.getHostAddress());
				}
			}
		}

		return ipSet;
	}

	/**
	 * 获得本机MAC地址
	 * 
	 * @return 本机MAC地址
	 */
	public static String getLocalMacAddress() {
		return getMacAddress(getLocalhost());
	}

	/**
	 * 获取本机网卡IP地址，这个地址为所有网卡中非回路地址的第一个<br>
	 * 如果获取失败调用 {@link InetAddress#getLocalHost()}方法获取。<br>
	 * 此方法不会抛出异常，获取失败将返回<code>null</code><br>
	 * 
	 * 参考：http://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
	 * 
	 * @return 本机网卡IP地址，获取失败返回<code>null</code>
	 * @since 3.0.7
	 */
	public static String getLocalhostStr() {
		InetAddress localhost = getLocalhost();
		if (null != localhost) {
			return localhost.getHostAddress();
		}
		return null;
	}

	/**
	 * 获取本机网卡IP地址，这个地址为所有网卡中非回路地址的第一个<br>
	 * 如果获取失败调用 {@link InetAddress#getLocalHost()}方法获取。<br>
	 * 此方法不会抛出异常，获取失败将返回<code>null</code><br>
	 * 
	 * 参考：http://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
	 * 
	 * @return 本机网卡IP地址，获取失败返回<code>null</code>
	 * @since 3.0.1
	 */
	public static InetAddress getLocalhost() {
		InetAddress candidateAddress = null;
		NetworkInterface iface;
		InetAddress inetAddr;
		try {
			for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces
					.hasMoreElements();) {
				iface = ifaces.nextElement();
				for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
					inetAddr = inetAddrs.nextElement();
					if (false == inetAddr.isLoopbackAddress()) {
						if (inetAddr.isSiteLocalAddress()) {
							return inetAddr;
						} else if (null == candidateAddress) {
							// 非site-local地址做为候选地址返回
							candidateAddress = inetAddr;
						}
					}
				}
			}
		} catch (SocketException e) {
			// ignore socket exception, and return null;
		}

		if (null == candidateAddress) {
			try {
				candidateAddress = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				// ignore
			}
		}

		return candidateAddress;
	}

	/**
	 * 获得指定地址信息中的MAC地址，使用分隔符“-”
	 * 
	 * @param inetAddress
	 *            {@link InetAddress}
	 * @return MAC地址，用-分隔
	 */
	public static String getMacAddress(InetAddress inetAddress) {
		return getMacAddress(inetAddress, "-");
	}

}
