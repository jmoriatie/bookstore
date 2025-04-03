package com.solve.bookstore.application.security;

import com.solve.bookstore.domain.user.model.User;
import com.solve.bookstore.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookstoreUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("[login email] {} ", email);
        if (email == null || email.trim().isEmpty()) {
            throw new UsernameNotFoundException("email 을 입력해주세요.");
        }

        User user = userRepository.findByEmail(email);
        return new BookstoreUserDetails(user);
    }
}