package com.isagron.security;

import com.isagron.security.configuration.properties.SecurityProperties;
import com.isagron.security.domain.entities.Authority;
import com.isagron.security.domain.entities.Role;
import com.isagron.security.domain.repositories.AuthorityRepository;
import com.isagron.security.domain.repositories.RoleRepository;
import com.isagron.security.domain.types.DefaultRoleType;
import com.isagron.security.services.resources.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AppStartupRunner implements ApplicationRunner {

    @Value("${app.user.image.folder}")
    private String imageFolderPath;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        File file = new File(imageFolderPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        loadDefaultRolesAndAuthorities();

        if (securityProperties.getFirstUser() != null &&
                securityProperties.getFirstUser().getEmail() != null &&
                !userService.findUserByEmail(securityProperties.getFirstUser().getEmail()).isPresent()) {
            this.userService.createUser(
                    securityProperties.getFirstUser().getFirstName(),
                    securityProperties.getFirstUser().getLastName(),
                    securityProperties.getFirstUser().getUserName(),
                    securityProperties.getFirstUser().getEmail(),
                    bCryptPasswordEncoder.encode(securityProperties.getFirstUser().getPassword()),
                    true,
                    false,
                    false,
                    DefaultRoleType.ROLE_SUPER_ADMIN.name(),
                    null);

        }
    }

    private void loadDefaultRolesAndAuthorities() {
        Map<String, Authority> authorityMap = Arrays.stream(DefaultRoleType.values())
                .flatMap(roleType -> roleType.getAuthorities().stream())
                .distinct()
                .map(authName -> {
                    if (this.authorityRepository.existsByName(authName)){
                        return this.authorityRepository.findByName(authName).get();
                    }
                    Authority authority = Authority.builder()
                            .name(authName)
                            .build();
                    return authorityRepository.save(authority);
                }).collect(Collectors.toMap(Authority::getName, authority -> authority));
        Arrays.stream(DefaultRoleType.values())
                .forEach(roleType -> {
                    if (!this.roleRepository.existsByName(roleType.name())) {
                        Role role = Role.builder()
                                .name(roleType.name())
                                .build();
                        List<Authority> authorities = authorityMap.entrySet().stream()
                                .filter(entry -> roleType.getAuthorities().contains(entry.getKey()))
                                .map(Map.Entry::getValue)
                                .collect(Collectors.toList());
                        role.addAuthorities(authorities);
                        this.roleRepository.save(role);
                    }
                });
    }
}
