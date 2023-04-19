package com.zoctan.api.core.rsa;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA 도구
 *
 * <p>openssl로 512비트 RSA 생성：
 *
 * <p>개인 키 생성: openssl genrsa -out key.pem 512
 *
 * <p>개인 키에서 공개 키로: openssl rsa -in key.pem -pubout -out public-key.pem
 *
 * <p>공개 키 암호화: openssl rsautl -encrypt -in xx.file -inkey public-key.pem -pubin -out xx.en
 *
 * <p>개인 키 복호화： openssl rsautl -decrypt -in xx.en -inkey key.pem -out xx.de
 *
 * <p>pkcs8코딩（Java）： openssl pkcs8 -topk8 -inform PEM -in key.pem -outform PEM -out private-key.pem
 * -nocrypt
 *
 * <p>마지막으로 공개 및 비공개 유에의/resources/rsa/：private-key.pem public-key.pem
 *
 * @author Zoctan
 * @date 2018/07/20
 */
@Slf4j
@Component
public class RsaUtils {
  @Resource private RsaConfigurationProperties rsaProperties;
  private static final String ALGORITHM = "RSA";
  private PrivateKey privateKey;
  private PublicKey publicKey;

  public RsaUtils() {
    if (this.rsaProperties == null) {
      this.rsaProperties = new RsaConfigurationProperties();
    }
  }

  /**
   * 키 쌍 생성
   *
   * @param keyLength 키 길이(최소 512비트)
   * @return 키 쌍 공개 키 keyPair.getPublic() 비공개 키 keyPair.getPrivate()
   * @throws Exception e
   */
  public KeyPair genKeyPair(final int keyLength) throws Exception {
    final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
    keyPairGenerator.initialize(keyLength);
    return keyPairGenerator.generateKeyPair();
  }

  /**
   * 공개 키 암호화
   *
   * @param content 암호화할 데이터
   * @param publicKey 공개 키
   * @return 암호화된 콘텐츠
   * @throws Exception e
   */
  public byte[] encrypt(final byte[] content, final PublicKey publicKey) throws Exception {
    final Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    return cipher.doFinal(content);
  }

  /**
   * 공개 키 암호화
   *
   * @param content 암호화할 데이터
   * @return 암호화된 콘텐츠
   * @throws Exception e
   */
  public byte[] encrypt(final byte[] content) throws Exception {
    return this.encrypt(content, this.publicKey != null ? this.publicKey : this.loadPublicKey());
  }

  /**
   * 개인 키 암호 해독
   *
   * @param content 암호화된 데이터
   * @param privateKey 개인 키
   * @return 기밀 해제된 콘텐츠
   * @throws Exception e
   */
  public byte[] decrypt(final byte[] content, final PrivateKey privateKey) throws Exception {
    final Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    return cipher.doFinal(content);
  }

  /**
   * 개인 키 암호 해독
   *
   * @param content 암호화된 데이터
   * @return 기밀 해제된 콘텐츠
   * @throws Exception e
   */
  public byte[] decrypt(final byte[] content) throws Exception {
    return this.decrypt(content, this.privateKey != null ? this.privateKey : this.loadPrivateKey());
  }

  /**
   * 공개 키를 pem 형식으로 로드합니다.
   *
   * @param decoded 바이너리 공개 키
   * @return 공개 키
   */
  public PublicKey loadPublicKey(final byte[] decoded) {
    try {
      final X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
      final KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
      this.publicKey = keyFactory.generatePublic(spec);
      return this.publicKey;
    } catch (final Exception e) {
      log.error("==> RSA Utils Exception: {}", e.getMessage());
      return null;
    }
  }

  /**
   * 구성 파일에 설정된 공개 키를 로드합니다.
   *
   * @return 공개 키
   */
  public PublicKey loadPublicKey() {
    try {
      byte[] decoded;
      if (this.rsaProperties.isUseFile()) {
        decoded =
            this.replaceAndBase64Decode(
                this.rsaProperties.getPublicKeyPath(),
                this.rsaProperties.getPublicKeyHead(),
                this.rsaProperties.getPublicKeyTail());
      } else {
        decoded = Base64.decodeBase64(this.rsaProperties.getPublicKey());
      }
      return this.loadPublicKey(decoded);
    } catch (final Exception e) {
      log.error("==> RSA Utils Exception: {}", e.getMessage());
      return null;
    }
  }

  /**
   * pem 형식 PKCS8 인코딩된 개인 키 로드
   *
   * @param decoded 바이너리 개인 키
   * @return 개인 키
   */
  public PrivateKey loadPrivateKey(final byte[] decoded) {
    try {
      final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
      final KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
      this.privateKey = keyFactory.generatePrivate(spec);
      return this.privateKey;
    } catch (final Exception e) {
      log.error("==> RSA Utils Exception: {}", e.getMessage());
      return null;
    }
  }

  /**
   * 구성 파일에 설정된 개인 키를 로드합니다.
   *
   * @return 개인 키
   */
  public PrivateKey loadPrivateKey() {
    try {
      byte[] decoded;
      if (this.rsaProperties.isUseFile()) {
        decoded =
            this.replaceAndBase64Decode(
                this.rsaProperties.getPrivateKeyPath(),
                this.rsaProperties.getPrivateKeyHead(),
                this.rsaProperties.getPrivateKeyTail());
      } else {
        decoded = Base64.decodeBase64(this.rsaProperties.getPrivateKey());
      }
      return this.loadPrivateKey(decoded);
    } catch (final Exception e) {
      log.error("==> RSA Utils Exception: {}", e.getMessage());
      return null;
    }
  }

  /**
   * 파일을 로드하고 헤더와 꼬리를 바꾸고 암호를 해독합니다.
   *
   * @return 파일 바이트
   */
  private byte[] replaceAndBase64Decode(
      final String filePath, final String headReplace, final String tailReplace) throws Exception {
    // 从 classpath:resources/ 中加载资源
    final ClassPathResource resource = new ClassPathResource(filePath);
    if (!resource.exists()) {
      throw new Exception("공개 키 및 개인 키 파일을 찾을 수 없습니다.");
    }
    final byte[] keyBytes = new byte[(int) resource.getFile().length()];
    final FileInputStream in = new FileInputStream(resource.getFile());
    in.read(keyBytes);
    in.close();

    final String keyPEM =
        new String(keyBytes).replace(headReplace, "").trim().replace(tailReplace, "").trim();

    return Base64.decodeBase64(keyPEM);
  }
}
