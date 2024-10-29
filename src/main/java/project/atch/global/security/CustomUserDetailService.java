package project.atch.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.user.entity.OAuthProvider;
import project.atch.domain.user.entity.User;
import project.atch.domain.user.repository.UserRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    // email과 oauthProvider로 사용자를 찾는 새로운 메서드 추가
    public UserDetails loadUserByEmailAndProvider(String email, String provider) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndOAuthProvider(email, OAuthProvider.valueOf(provider))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("This method is not supported. Use loadUserByEmailAndProvider instead.");
    }
}

