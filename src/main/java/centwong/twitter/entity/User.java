package centwong.twitter.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Table("user")
@Document(indexName = "user")
@ToString
@Setter
@Getter
@Builder
public class User extends Pageable{

    @Id
    private Long id;

    private String name;

    @Column("no_telephone_email")
    private String noTelephoneOrEmail;

    @JsonIgnore
    private String password;

    private String profilePicture;

    private String description;

    private LocalDate createdAt;

    private Long createdBy;

    private LocalDate updatedAt;

    private Long updatedBy;

    private LocalDate deletedAt;

    private Long deletedBy;

    @Transient
    private String jwtToken;
}
