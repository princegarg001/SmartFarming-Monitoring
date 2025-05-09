package com.ncu.SmartFarming_Montioring_System.Security;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class Authcontroller {

    private final AuthenticationManager manager;
    private final JwtUtil jwtUtil;
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public Authcontroller(AuthenticationManager manager, JwtUtil jwtUtil, UserRepository repo, PasswordEncoder encoder) {
        this.manager = manager;
        this.jwtUtil = jwtUtil;
        this.repo = repo;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");
            String role = body.get("role");

            if (username == null || password == null || role == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing fields");
            }

            var user = new User();
            user.setUsername(username);
            user.setPassword(encoder.encode(password));
            user.setRoles(Set.of(Role.valueOf(role.toUpperCase())));
            repo.save(user);
            return "User registered";
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role provided");
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing username or password");
        }

        manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        var user = repo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return jwtUtil.generateToken(
                user.getUsername(),
                user.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toSet())
        );
    }
}
