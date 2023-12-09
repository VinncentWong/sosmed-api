package centwong.twitter.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("user")
@Data
@Builder
public class User {

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
}
