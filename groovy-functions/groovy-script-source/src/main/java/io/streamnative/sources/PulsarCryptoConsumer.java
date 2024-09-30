package io.streamnative.sources;

import io.streamnative.functions.crypto.RawFileKeyReader;
import org.apache.pulsar.client.api.*;

import java.util.HashMap;
import java.util.Map;

public class PulsarCryptoConsumer {

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

        Consumer<String> consumer = client.newConsumer(Schema.STRING)
                .topic(TOPIC_NAME)
                .subscriptionName("foo")
                .subscriptionType(SubscriptionType.Exclusive)
                .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
                .cryptoKeyReader(keyReader)
                .subscribe();


        Message<String> msg;
        while ((msg = consumer.receive()) != null) {
            System.out.println("Consumed " + msg.getValue());
        }
    }

}
