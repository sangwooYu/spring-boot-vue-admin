package com.zoctan.api.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * IP 도구
 *
 * @author Zoctan
 * @date 2018/05/27
 */
public class IpUtils {
  private static final String UNKNOWN = "unknown";
  private static final String LOCALHOST_IPV4 = "127.0.0.1";
  private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

  private IpUtils() {}

  public static String getIpAddress() {
    return getIpAddress(ContextUtils.getRequest());
  }

  /**
   * 요청에서 IP 주소 가져오기
   *
   * @param request request
   * @return IP
   */
  public static String getIpAddress(final HttpServletRequest request) {
    String ip = LOCALHOST_IPV4;
    if (request != null) {
      ip = request.getHeader("x-forwarded-for");
      if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
        ip = request.getHeader("Proxy-Client-IP");
      }
      if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
        ip = request.getHeader("WL-Proxy-Client-IP");
      }
      if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
        ip = request.getHeader("HTTP_CLIENT_IP");
      }
      if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
      }
      if (StringUtils.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
        ip = request.getRemoteAddr();
        // request.getRemoteAddr() 대부분의 경우 클라이언트의 IP 주소를 얻는 것이 유효합니다.
        // 그러나 Apache, Squid 등과 같은 역방향 프록시를 사용하면 클라이언트의 실제 IP 주소를 얻을 수 없습니다.
        // 여러 레벨의 역방향 프록시가 전달되는 경우 둘 이상의 X-Forwarded-For 값이 있습니다.
        // 대신 192.168.1.110,192.168.1.120,192.168.1.130,192.168.1.100과 같은 IP 값의 문자열입니다.
        // 여기서 처음 192.168.1.110은 사용자의 실제 IP입니다.
        if (LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip)) {
          // 루프백 주소가 아닌 NIC를 기반으로 로컬로 구성된 IP를 사용합니다.
          try {
            ip = InetAddress.getLocalHost().getHostAddress();
          } catch (final UnknownHostException ignored) {
          }
        }
      }
      // 많은 IP 중 첫 번째
      final String ch = ",";
      if (!StringUtils.isEmpty(ip) && ip.contains(ch)) {
        ip = ip.substring(0, ip.indexOf(ch));
      }
    }
    return ip;
  }

  /**
   * IP로 관련 정보 얻기(인터넷 연결 필요, 타오바오의 IP 라이브러리 호출)
   *
   * @param ip ip
   * @return IP 관련 정보
   */
  public static String getInfoByIP(final String ip) {
    try {
      final URL url = new URL("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip);
      final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setUseCaches(false);

      final InputStream in = connection.getInputStream();
      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
      final StringBuilder buffer = new StringBuilder();
      String line = bufferedReader.readLine();
      while (line != null) {
        buffer.append(line).append("\r\n");
        line = bufferedReader.readLine();
      }
      bufferedReader.close();
      final JSONObject obj = (JSONObject) JSON.parse(buffer.toString());
      final StringBuilder info = new StringBuilder();
      final int responseCode = obj.getIntValue("code");
      if (responseCode == 0) {
        final JSONObject data = obj.getJSONObject("data");
        info.append(data.getString("country")).append(" ");
        info.append(data.getString("region")).append(" ");
        info.append(data.getString("city")).append(" ");
        info.append(data.getString("isp"));
      }
      return info.toString();
    } catch (final IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
