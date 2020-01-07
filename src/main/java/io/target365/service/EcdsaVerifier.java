package io.target365.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.DLSequence;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EcdsaVerifier implements Verifier {

    private static final int RADIX = 16;

    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";
    private static final String KEY_FACTORY_ALGORITHM = "EC";

    private final PublicKey publicKey;

    @Override
    public boolean verify(final String message, final String sign) {
        try {
            final Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(message.getBytes(StandardCharsets.UTF_8));

            final String xy = DatatypeConverter.printHexBinary(DatatypeConverter.parseBase64Binary(sign));
            final String x = xy.substring(0, 64);
            final String y = xy.substring(64, xy.length());

            final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
            asn1EncodableVector.add(new ASN1Integer(new BigInteger(x, RADIX)));
            asn1EncodableVector.add(new ASN1Integer(new BigInteger(y, RADIX)));

            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final ASN1OutputStream asn1OutputStream = new ASN1OutputStream(byteArrayOutputStream);
            asn1OutputStream.writeObject(new DLSequence(asn1EncodableVector));

            final byte[] bytes = byteArrayOutputStream.toByteArray();

            asn1OutputStream.flush();
            asn1OutputStream.close();
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();

            return signature.verify(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static EcdsaVerifier getInstance(final String ecPublicKeyAsString) {
        try {
            final String rawEcPublicKey = ecPublicKeyAsString
                .replaceAll("-----BEGIN EC PUBLIC KEY-----", "")
                .replaceAll("-----END EC PUBLIC KEY-----", "")
                .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("\n", "");

            final X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(DatatypeConverter.parseBase64Binary(rawEcPublicKey));

            return new EcdsaVerifier(KeyFactory.getInstance(KEY_FACTORY_ALGORITHM).generatePublic(x509EncodedKeySpec));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
