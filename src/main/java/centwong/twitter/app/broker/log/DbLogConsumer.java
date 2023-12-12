package centwong.twitter.app.broker.log;

import centwong.twitter.entity.DbLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

@Component
@Slf4j
public class DbLogConsumer {

    private final ObjectMapper mapper;

    private final ReactiveElasticsearchOperations operations;

    private final LogRepository repository;

    @Autowired
    public DbLogConsumer(ObjectMapper mapper, ReactiveElasticsearchOperations operations, LogRepository repository) {
        this.mapper = mapper;
        this.operations = operations;
        this.repository = repository;
    }

    @SneakyThrows
    @KafkaListener(topics = "user")
    public void consume(String message){
        var logs = this.mapper.readValue(message, DbLog.class);
        log.info("save {} on class {}", logs, DbLogConsumer.class.getSimpleName());
        this
                .operations
                .save(logs)
                .then(
                        this.repository
                                .save(logs)
                )
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe((v) -> {
                    log.info("success save {}", logs);
                });
    }
}
