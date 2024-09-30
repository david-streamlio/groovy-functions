package io.streamnative.sources;

import io.streamnative.functions.crypto.RawFileKeyReader;
import io.streamnative.sources.data.*;

import org.apache.pulsar.client.api.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class PulsarCryptoProducer {

    private static final String PULSAR_SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "persistent://public/default/groovy-scripts";

    public static void main(String[] args) throws Exception {

        Map<String, Object> config = new HashMap<>();
        config.put("publicKeyFile", "key_c_pubkey.pem");
        config.put("privateKeyFile", "key_c_privkey.pem");

        // Create a CryptoKeyReader instance
        CryptoKeyReader keyReader = new RawFileKeyReader(config);

        // Create a Pulsar client
        PulsarClient client = PulsarClient.builder()
                .serviceUrl(PULSAR_SERVICE_URL)
                .build();

        // Create a producer with encryption enabled
        Producer<String> producer = client.newProducer(Schema.STRING)
                .topic(TOPIC_NAME)
                .cryptoKeyReader(keyReader)
                .addEncryptionKey("key_c")  // The key name
                .create();

        GroovyScriptRepository repository = new RandomScriptRepository();
        // Message to publish
        String message = null;

        while ( (message = repository.get()) != null) {
            System.out.println("Sending " + message);
            // Publish encrypted message
            producer.send(message);
            Thread.sleep(5000);
        }

        // Close the producer and client
        producer.close();
        client.close();
    }
}
