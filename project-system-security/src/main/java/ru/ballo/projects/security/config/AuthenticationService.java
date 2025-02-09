package ru.ballo.projects.security.config;

import ru.ballo.projects.dto.authenticate.AuthenticationRequest;
import ru.ballo.projects.dto.authenticate.AuthenticationResponse;
import ru.ballo.projects.dto.authenticate.RegisterRequest;
import ru.ballo.projects.mapping.MemberMapper;
import ru.ballo.projects.models.Member;
import ru.ballo.projects.models.MemberDetails;
import ru.ballo.projects.repos.JpaRepos.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final MemberJpaRepository memberRepository;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        Member member = memberMapper.create(request);
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        memberRepository.save(member);

        UserDetails memberDetails = new MemberDetails(member);
        String jwtToken = jwtService.generateToken(memberDetails);
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow();
        UserDetails memberDetails = new MemberDetails(member);
        String jwtToken = jwtService.generateToken(memberDetails);
        return new AuthenticationResponse(jwtToken);
    }
}
