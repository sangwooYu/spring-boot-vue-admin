package com.zoctan.api.util;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;

/**
 * AES 암호화 및 복호화 도구
 *
 * @author Zoctan
 * @date 2018/11/29
 */
public class AesUtils {
  static {
    // http://www.bouncycastle.org/
    Security.addProvider(new BouncyCastleProvider());
  }

  /**
   * AES 복호화
   *
   * @param data 암호 텍스트, 암호화된 데이터
   * @param key 비밀 키
   * @param iv 오프셋
   * @param encodingFormat 복호화된 결과에 필요한 인코딩
   */
  public static String decrypt(
      final String data, final String key, final String iv, final Charset encodingFormat) {
    // 被加密的数据
    final byte[] dataByte = Base64.decodeBase64(data);
    // 加密秘钥
    final byte[] keyByte = Base64.decodeBase64(key);
    // 偏移量
    final byte[] ivByte = Base64.decodeBase64(iv);
    try {
      final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
      final SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
      final AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
      parameters.init(new IvParameterSpec(ivByte));
      cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
      final byte[] resultByte = cipher.doFinal(dataByte);
      if (null != resultByte && resultByte.length > 0) {
        return new String(resultByte, encodingFormat);
      }
      return null;
    } catch (final NoSuchAlgorithmException
        | NoSuchPaddingException
        | InvalidParameterSpecException
        | InvalidKeyException
        | InvalidAlgorithmParameterException
        | IllegalBlockSizeException
        | BadPaddingException
        | NoSuchProviderException e) {
      e.printStackTrace();
    }
    return null;
  }
}
