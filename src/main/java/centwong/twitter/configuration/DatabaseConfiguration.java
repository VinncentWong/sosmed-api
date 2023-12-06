package centwong.twitter.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        R2dbcProperties.class, FlywayProperties.class
})
public class DatabaseConfiguration {

    @Bean(initMethod = "migrate")
    public Flyway flyway(
            FlywayProperties props, R2dbcProperties r2dbcProps
    ){
        return Flyway
                .configure()
                .dataSource(
                        props.getUrl(),
                        r2dbcProps.getUsername(),
                        r2dbcProps.getPassword()
                )
                .baselineOnMigrate(true)
                .load();
    }
}
