package com.example.messenger.UserDemo;

import com.example.messenger.user.IUserService;
import com.example.messenger.user.RoleRepository;
import com.example.messenger.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class DemoController {
    @Autowired
    private IUserService userService;
    @Autowired
    private RoleRepository repository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping
    public ResponseEntity<?> demo() {
//        throw new RuntimeException();
        return ResponseEntity.ok( userRepository.findAll());
    }
}
