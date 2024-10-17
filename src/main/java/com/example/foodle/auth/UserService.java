package com.example.foodle.auth;

import com.example.foodle.FileHandlerUtils;
import com.example.foodle.auth.dto.*;
import com.example.foodle.auth.entity.CustomUserDetails;
import com.example.foodle.auth.entity.UserEntity;
import com.example.foodle.auth.jwt.JwtTokenUtils;
import com.example.foodle.auth.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    private final AuthenticationFacade authFacade;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final FileHandlerUtils fileHandlerUtils;

    @Autowired
    public UserService(AuthenticationFacade authFacade, UserRepo userRepo, PasswordEncoder passwordEncoder, JwtTokenUtils jwtTokenUtils, FileHandlerUtils fileHandlerUtils) {
        this.authFacade = authFacade;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtils = jwtTokenUtils;
        this.fileHandlerUtils = fileHandlerUtils;


        createAdminAccount();
    }

    private void createAdminAccount() {
        //kiemtra xem user ton tai chua
        if (!userRepo.existsByUsername("admin")) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("securePassword"));
            admin.setRoles("ROLE_ADMIN");

            // luu vao db
            userRepo.save(admin);
            System.out.println("Default admin account created.");
        }
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .map(CustomUserDetails::fromEntity)
                .orElseThrow(() -> new UsernameNotFoundException("not found"));
    }

    @Transactional
    public UserDto createUser(CreateUserDto dto) {
        if (!dto.getPassword().equals(dto.getPasswordCheck()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (userRepo.existsByUsername(dto.getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return UserDto.fromEntity(userRepo.save(UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .roles("ROLE_USER")
                .build()));
    }
    @Transactional
    public UserDto createOwner(CreateUserDto dto) {
        if (!dto.getPassword().equals(dto.getPasswordCheck()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (userRepo.existsByUsername(dto.getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return UserDto.fromEntity(userRepo.save(UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .roles("ROLE_OWNER")
                .build()));
    }

    public JwtResponseDto signin(JwtRequestDto dto) {
        UserEntity userEntity = userRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(
                dto.getPassword(),
                userEntity.getPassword()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        String jwt = jwtTokenUtils.generateToken(CustomUserDetails.fromEntity(userEntity));
        JwtResponseDto response = new JwtResponseDto();
        response.setToken(jwt);
        return response;
    }

    public UserDto updateUser(UpdateUserDto dto){
        UserEntity userEntity = authFacade.extractUser();
        userEntity.setNickname(dto.getNickname());
        userEntity.setName(dto.getName());
        userEntity.setAge(dto.getAge());
        userEntity.setPhone(dto.getPhone());
        userEntity.setEmail(dto.getEmail());
        if (
                userEntity.getNickname() != null &&
                userEntity.getName() != null &&
                userEntity.getAge() != null &&
                userEntity.getEmail() != null &&
                userEntity.getPhone() != null &&
                userEntity.getRoles().equals("ROLE_INACTIVE")
        )
                userEntity.setRoles("ROLE_ACTIVE");
        return UserDto.fromEntity(userRepo.save(userEntity));
    }


    public UserDto profileImg(MultipartFile file) {
        UserEntity userEntity = authFacade.extractUser();
        String requestPath = fileHandlerUtils.saveFile(
                String.format("users/%d/", userEntity.getId()),
                "profile",
                file
        );

        userEntity.setProfileImg(requestPath);
        return UserDto.fromEntity(userRepo.save(userEntity));
    }

    public UserDto getUserInfo() {
        return UserDto.fromEntity(authFacade.extractUser());
    }
}
