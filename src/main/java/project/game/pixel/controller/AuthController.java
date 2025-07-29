package project.game.pixel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.game.pixel.dto.request.SignupRequestDto;
import project.game.pixel.dto.request.LoginRequestDto;
import project.game.pixel.service.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<? super SignupResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        return userService.signup(requestDto);
    }

    @PostMapping("/login")
    public ResponseEntity<? super LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        return userService.login(requestDto);
    }
}
