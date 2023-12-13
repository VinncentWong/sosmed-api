package centwong.twitter.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    public ObjectMapper mapper(){
        var objMapper = new ObjectMapper();
        objMapper.registerModule(new JavaTimeModule());
        objMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        return objMapper;
    }
}
