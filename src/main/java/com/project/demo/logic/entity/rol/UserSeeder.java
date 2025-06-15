package com.project.demo.logic.entity.rol;

import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createDefaultUser();
    }

    private void createDefaultUser() {
        String email = "user@gmail.com";

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            System.out.println("UserSeeder: El usuario por defecto ya existe.");
            return;
        }

        Role role = roleRepository.findByName(RoleEnum.USER)
                .orElseGet(() -> {
                    System.out.println("UserSeeder: Rol USER no encontrado. Creando uno nuevo.");
                    Role newRole = new Role();
                    newRole.setName(RoleEnum.USER);
                    newRole.setDescription("Default user role");
                    return roleRepository.save(newRole);
                });

        User user = new User();
        user.setName("Default");
        user.setLastname("User");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("user1234"));
        user.setRole(role);

        userRepository.save(user);
        System.out.println("UserSeeder: Usuario por defecto creado con éxito.");
    }

}
