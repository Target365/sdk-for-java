package io.target365.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DLSequence;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EcdsaSigner implements Signer {

    private static final int RADIX = 16;

    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";
    private static final String KEY_FACTORY_ALGORITHM = "EC";

    private final PrivateKey privateKey;

    @Override
    public String sign(final String message) {
        try {
            final Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(message.getBytes(StandardCharsets.UTF_8));

            final byte[] bytes = signature.sign();
            final DLSequence dlSequence = (DLSequence) new ASN1InputStream(bytes).readObject();

            final String x = String.format("%64s", ((ASN1Integer) dlSequence.getObjectAt(0)).getPositiveValue().toString(RADIX))
                .replaceAll(" ", "0").toUpperCase();
            final String y = String.format("%64s", ((ASN1Integer) dlSequence.getObjectAt(1)).getPositiveValue().toString(RADIX))
                .replaceAll(" ", "0").toUpperCase();

            return DatatypeConverter.printBase64Binary(DatatypeConverter.parseHexBinary(x + y));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static EcdsaSigner getInstance(final String ecPrivateKeyAsString) {
        try {
            final String rawEcPrivateKey = ecPrivateKeyAsString
                .replaceAll("-----BEGIN EC PRIVATE KEY-----", "")
                .replaceAll("-----END EC PRIVATE KEY-----", "")
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\n", "");

            final PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(DatatypeConverter.parseBase64Binary(rawEcPrivateKey));

            return new EcdsaSigner(KeyFactory.getInstance(KEY_FACTORY_ALGORITHM).generatePrivate(pkcs8EncodedKeySpec));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

}
