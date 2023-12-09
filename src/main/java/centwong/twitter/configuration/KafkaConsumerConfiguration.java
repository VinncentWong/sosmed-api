package centwong.twitter.configuration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;

@Configuration
public class KafkaConsumerConfiguration {

    @Value("${kafka.bootstrap_server}")
    private String boostrapServer;

    @Value("${kafka.username}")
    private String username;

    @Value("${kafka.password}")
    private String password;

    @Bean
    public ConsumerFactory<String, String> consumerFactory(){
        var config = new HashMap<String, Object>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.boostrapServer);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put("sasl.mechanism", "SCRAM-SHA-256");
        config.put("security.protocol", "SASL_SSL");
        config.put("sasl.jaas.config", String.format("org.apache.kafka.common.security.scram.ScramLoginModule required username=%s password=%s;", this.username, this.password));
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerConfig(){
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
