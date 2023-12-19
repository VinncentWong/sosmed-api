package centwong.twitter.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ObjectMapperUtil {

    @Autowired
    private ObjectMapper mapper;

    @SneakyThrows
    public String writeValueAsString(Object obj){
        return this.mapper.writeValueAsString(obj);
    }

    @SneakyThrows
    public <T> T readValue(String str, Class<T> clazz){
        return this.mapper.readValue(str, clazz);
    }

    @SneakyThrows
    public <T> List<T> readListValue(String str){
        return this.mapper.readValue(str, new TypeReference<>() {});
    }
}
