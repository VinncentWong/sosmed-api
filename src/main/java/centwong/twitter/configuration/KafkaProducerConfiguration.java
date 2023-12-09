package centwong.twitter.configuration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;

@Configuration
public class KafkaProducerConfiguration {

    @Value("${kafka.bootstrap_server}")
    private String boostrapServer;

    @Value("${kafka.username}")
    private String username;

    @Value("${kafka.password}")
    private String password;

    @Bean
    public ProducerFactory<String, String> producerFactory(){
        var config = new HashMap<String, Object>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put("sasl.mechanism", "SCRAM-SHA-256");
        config.put("security.protocol", "SASL_SSL");
        config.put("sasl.jaas.config", String.format("org.apache.kafka.common.security.scram.ScramLoginModule required username=%s password=%s;", this.username, this.password));
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(){
        return new KafkaTemplate<>(this.producerFactory());
    }
}
