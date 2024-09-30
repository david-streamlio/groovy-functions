package io.streamnative.functions.crypto;

import org.apache.pulsar.client.api.CryptoKeyReader;
import org.apache.pulsar.client.api.EncryptionKeyInfo;

import java.util.Map;

public class RawFileKeyReader implements CryptoKeyReader {

    private final String publicKeyFile;
    private final String privateKeyFile;

    public RawFileKeyReader(Map<String, Object> properties) {
        if (properties == null || !properties.containsKey("publicKeyFile") ||
        !properties.containsKey("privateKeyFile")) {
            throw new RuntimeException("Missing keys");
        }

        this.publicKeyFile = (String) properties.get("publicKeyFile");
        this.privateKeyFile = (String) properties.get("privateKeyFile");
    }

    public RawFileKeyReader(String pubKeyFile, String privKeyFile) {
        publicKeyFile = pubKeyFile;
        privateKeyFile = privKeyFile;
    }

    @Override
    public EncryptionKeyInfo getPublicKey(String keyName, Map<String, String> keyMeta) {
        EncryptionKeyInfo keyInfo = new EncryptionKeyInfo();
        try {
            // keyInfo.setKey(Files.readAllBytes(Path.of(ClassLoader.getSystemResource(publicKeyFile).toURI())));
            keyInfo.setKey(ResourceLoader.loadResourceAsBytes(publicKeyFile));
        } catch (Exception e) {
            System.out.println("ERROR: Failed to read public key from file " + publicKeyFile);
            e.printStackTrace();
        }
        return keyInfo;
    }

    @Override
    public EncryptionKeyInfo getPrivateKey(String keyName, Map<String, String> keyMeta) {
        EncryptionKeyInfo keyInfo = new EncryptionKeyInfo();
        try {
            // keyInfo.setKey(Files.readAllBytes(Path.of(ClassLoader.getSystemResource(privateKeyFile).toURI())));
            keyInfo.setKey(ResourceLoader.loadResourceAsBytes(privateKeyFile));
        } catch (Exception e) {
            System.out.println("ERROR: Failed to read private key from file " + privateKeyFile);
            e.printStackTrace();
        }
        return keyInfo;
    }
}

