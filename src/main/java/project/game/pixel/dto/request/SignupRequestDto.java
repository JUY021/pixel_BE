package project.game.pixel.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter @Setter
@NoArgsConstructor
public class SignupRequestDto {

    @NotBlank
    private String userId;       // 로그인 아이디

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,13}$")
    private String password;     // 비밀번호 (최소 8자 이상 등 유효성 체크 권장)

    @NotBlank
    private String userName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String nickname;     // 게임용 닉네임

    private String profileImageUrl;      // 프로필 이미지 URL (선택)

         // 이메일 (선택)
}
