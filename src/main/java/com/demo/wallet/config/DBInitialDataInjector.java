package com.demo.wallet.config;

import com.demo.wallet.model.Role;
import com.demo.wallet.model.User;
import com.demo.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DBInitialDataInjector implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        final User employee = User.builder()
                .name("Mert")
                .surname("Kara")
                .tckn("1111111111")
                .username("mert")
                .password(passwordEncoder.encode("444"))
                .roles(Set.of(Role.EMPLOYEE))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();

        userRepository.save(employee);

        final User customerAyse = User.builder()
                .name("Ayse")
                .surname("Bal")
                .tckn("22222222222")
                .username("ayse")
                .password(passwordEncoder.encode("123"))
                .roles(Set.of(Role.CUSTOMER))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();

        userRepository.save(customerAyse);

        final User customerFatma = User.builder()
                .name("Fatma")
                .surname("Kuzu")
                .tckn("3333333333")
                .username("fatma")
                .password(passwordEncoder.encode("567"))
                .roles(Set.of(Role.CUSTOMER))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();

        userRepository.save(customerFatma);

        log.info("db data injected");
    }
}
