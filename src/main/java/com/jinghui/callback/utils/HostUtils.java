package com.jinghui.callback.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

/**
 * @Description: 获取IP
 * @Author: JingHui Lin
 * @Date: 2020/5/6 10:17
 * @Version V1.0
 */
public class HostUtils {

    private static final Logger logger = LoggerFactory.getLogger(HostUtils.class);

    /**
     * 获取服务器IP地址
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<String> getServerIpList() {
        List<String> serverIpList = Lists.newArrayList();
        try {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress ip = inetAddresses.nextElement();
                    if (StringUtils.isNotEmpty(ip.getHostAddress()) && !ip.isLoopbackAddress()
                            && !ip.getHostAddress().contains(":")) {
                        serverIpList.add(ip.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        logger.info("获取到当前server的ip为：{}", serverIpList);
        return serverIpList;
    }

    public static String getServerIp() {
        return getServerIpList().get(0);
    }

    public static String getHostName() {
        if (System.getenv("COMPUTERNAME") != null) {
            return System.getenv("COMPUTERNAME");
        } else {
            return getHostNameForLinux();
        }
    }

    public static String getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return StringUtils.substringBefore(name, "@");
    }

    private static String getHostNameForLinux() {
        try {
            return (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException uhe) {
            // host = "hostname: hostname"
            String host = uhe.getMessage();
            if (host != null) {
                int colon = host.indexOf(':');
                if (colon > 0) {
                    return host.substring(0, colon);
                }
            }
            return "UnknownHost";
        }
    }
}
