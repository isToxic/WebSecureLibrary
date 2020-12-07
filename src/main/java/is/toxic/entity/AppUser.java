package is.toxic.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Entity
@Table
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long userId;

    @NotNull
    private String userName;

    @NotNull
    private String encrytedPassword;

    @Transient
    private String confirmPassword;

    @NotNull
    private boolean enabled;

}
