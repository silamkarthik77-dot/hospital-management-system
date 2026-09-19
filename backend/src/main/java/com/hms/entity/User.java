package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(length = 20)
    private String phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private boolean accountNonLocked = true;

    private String resetPasswordToken;

    private java.time.LocalDateTime resetPasswordTokenExpiry;


    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
            new SimpleGrantedAuthority("ROLE_" + role.getName().name())
        );
    }


    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }


    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }


    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return enabled;
    }
}