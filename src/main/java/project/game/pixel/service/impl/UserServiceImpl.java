package project.game.pixel.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import project.game.pixel.dto.request.SignupRequestDto;
import project.game.pixel.dto.request.LoginRequestDto;
import org.springframework.transaction.annotation.Transactional;
import project.game.pixel.dto.UserInfoDto;
import project.game.pixel.dto.UserUpdateDto;
import project.game.pixel.entity.User;
import project.game.pixel.exception.InvalidPasswordException;
import project.game.pixel.exception.UserAlreadyExistsException;
import project.game.pixel.exception.UserNotFoundException;
import project.game.pixel.mapper.UserMapper;
import project.game.pixel.repository.UserRepository;
import project.game.pixel.service.UserService;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ResponseEntity<? super SignupResponseDto> signup(SignupRequestDto dto) {
        if (userRepository.findByUserId(dto.getUserId()).isPresent()) {
            // 예외를 던지지 않고 응답 객체로 반환하려면 아래처럼 처리할 수도 있음
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(SignupResponseDto.fail("이미 존재하는 사용자입니다."));
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user = UserMapper.toEntity(dto, encodedPassword);
        userRepository.save(user);

        SignupResponseDto response = SignupResponseDto.success(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<? super LoginResponseDto> login(LoginRequestDto requestDto) {
        User user = userRepository.findByUserId(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 올바르지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getUserId());

        LoginResponseDto response = LoginResponseDto.success(token);
        return ResponseEntity.ok(response);
    }


    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<? super UserInfoDto> getUserInfo(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        UserInfoDto userInfo = UserMapper.toUserInfoDto(user);
        return ResponseEntity.ok(userInfo);
    }

    @Override
    public ResponseEntity<Void> updateUserInfo(String userId, UserUpdateDto updateDto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        UserMapper.updateUserFromDto(user, updateDto);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
