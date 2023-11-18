package pl.noteally.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name="users")
public class User implements UserDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<Catalog> catalogs;

    @Basic
    @NotBlank(message = "Must not be empty.")
    @Column(name = "login", nullable = false)
    @Size(min = 3, max = 20, message = "Size must be between 3 and 20.")
    @Pattern(regexp = "^[a-z]+$", message = "Login must contain only small letters.")
    private String login;

    @Basic
    @NotBlank(message = "Password is required.")
    @Size(min = 6, message = "Must be longer than 5 letters.")
    @Column(name = "password", nullable = false)
    private String password;

    @Basic
    @NotBlank(message = "Must not be empty.")
    @Column(name = "name", nullable = false)
    @Size(min = 3, max = 20, message = "Size must be between 3 and 20")
    @Pattern(regexp = "^[A-Z][a-z]*$", message = "Name must start with capital letter and contain only letters.")
    private String name;

    @Basic
    @NotBlank(message = "Must not be empty.")
    @Column(name = "surname", nullable = false)
    @Size(min = 3, max = 50, message = "Size must be between 3 and 50.")
    @Pattern(regexp = "^[A-Z][a-z]*$", message = "Name must start with capital letter and contain only letters.")
    private String surname;

    @Column(name = "age", nullable = false)
    @NotNull(message = "Age is required.")
    @Min(value = 18, message = "You must be 18 or older.")
    @Max(value = 150, message = "Age cannot exceed 150.")
    private Integer age;


    @Basic
    @Column(name = "role", nullable = false, columnDefinition = "varchar(7) default 'LIMITED'")
    @Enumerated(EnumType.STRING)
    private Role role = Role.LIMITED;
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", age=" + age +
                ", role=" + role +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

