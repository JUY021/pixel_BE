package project.game.pixel.entity;

import jakarta.persistence.*;
import lombok.*;
import project.game.pixel.dto.request.SignupRequestDto;

import java.time.LocalDateTime;

@Entity
@Table(name = "USERS") // 테이블명 직접 지정 권장, user는 예약어일 수 있음
@Data
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NUMBER_ID")
    private Long numberId;

    @Column(name = "USER_ID", unique = true, nullable = false)
    private String userId;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "NICKNAME", nullable = false)
    private String nickname;

    @Column(name = "PROFILE_IMAGE_URL")
    private String profileImageUrl;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "ROLE")
    private String role;

    public User (SignupRequestDto dto) {
        this.userId = dto.getUserId();
        this.password = dto.getPassword();
        this.name = dto.getUserName();
        this.email = dto.getEmail();
        this.nickname = dto.getNickname();
        this.profileImageUrl = dto.getProfileImageUrl();
        this.createdAt = LocalDateTime.now();
        this.role = "ROLE_USER";
    }
}
