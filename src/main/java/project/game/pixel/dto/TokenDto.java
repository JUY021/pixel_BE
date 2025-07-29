package project.game.pixel.dto;

import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class TokenDto {
    private String accessToken;
    private String refreshToken;

}
