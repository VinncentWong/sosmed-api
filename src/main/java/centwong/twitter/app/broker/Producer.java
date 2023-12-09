package centwong.twitter.app.broker;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    private final KafkaTemplate<String, String> template;

    private final ObjectMapper mapper;

    @Autowired
    public Producer(KafkaTemplate<String, String> template, ObjectMapper mapper) {
        this.template = template;
        this.mapper = mapper;
    }

    @SneakyThrows
    public void sendMessage(String topic, Object data){
        var str = this.mapper.writeValueAsString(data);
        this.template.send(topic, str);
    }
}
