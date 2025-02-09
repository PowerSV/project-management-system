package ru.ballo.projects.security.config;

import ru.ballo.projects.dto.authenticate.RegisterRequest;
import ru.ballo.projects.mapping.MemberMapper;
import ru.ballo.projects.models.Member;
import ru.ballo.projects.repos.JpaRepos.MemberJpaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final MemberMapper memberMapper;
    private final MemberJpaRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void createAdmin() {
        Optional<Member> admin = memberRepository.findByEmail("admin@admin.com");
        if (admin.isEmpty()) {
            RegisterRequest registerAdmin = new RegisterRequest();
            registerAdmin.setFirstName("admin");
            registerAdmin.setLastName("admin");
            registerAdmin.setEmail("admin@admin.com");
            registerAdmin.setPassword("admin");

            Member member = memberMapper.create(registerAdmin);
            member.setAuthoritiesRole("ROLE_ADMIN");
            member.setPassword(passwordEncoder.encode(registerAdmin.getPassword()));
            memberRepository.save(member);
        }
    }
}
