package project.atch.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import project.atch.domain.user.entity.User;

import java.util.Collection;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final Collection<GrantedAuthority> authorities;

    public CustomUserDetails(User user, Collection<GrantedAuthority> authorities) {
        this.userId = user.getId();
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return userId.toString();
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
