package io.stricte.jogging.domain;

import com.google.common.collect.Sets;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Data
@Entity
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Pattern(regexp = "^[a-z0-9\\-]{2,}$", flags = Pattern.Flag.CASE_INSENSITIVE)
    @Column(length = 100, unique = true, nullable = false)
    private String login;

    @NotNull
    @Size(min = 10, max = 60)
    @Column(name = "password", length = 60)
    private String password;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Email
    @Size(max = 100)
    @Column(length = 100, unique = true)
    private String email;

    @NotNull
    @Column(nullable = false)
    private boolean activated = false;

    @ManyToMany
    @JoinTable(
        name = "user_authority",
        joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") },
        inverseJoinColumns = { @JoinColumn(name = "authority_name", referencedColumnName = "name") })
    private Set<Authority> authorities = Sets.newHashSet();
}