package centwong.twitter.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Document(indexName = "db_log")
@Table(name = "db_log")
@NoArgsConstructor
@AllArgsConstructor
public class DbLog {

    @Id
    private String id;

    private String operation;

    private String tableName;

    private String message;
}
