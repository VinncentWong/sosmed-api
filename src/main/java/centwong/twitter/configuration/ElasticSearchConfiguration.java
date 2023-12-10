package centwong.twitter.configuration;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration;

@Configuration
public class ElasticSearchConfiguration extends ReactiveElasticsearchConfiguration {

    @Value("${elastic.host}")
    private String host;

    @Value("${elastic.port}")
    private String port;

    @Value("${elastic.username}")
    private String username;

    @Value("${elastic.password}")
    private String password;

    @Override
    public ClientConfiguration clientConfiguration() {
        return  ClientConfiguration
                .builder()
                .connectedTo(String.format("%s:%s", host, port))
                .withBasicAuth(username, password)
                .build();
    }
}
