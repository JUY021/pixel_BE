package project.game.pixel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.game.pixel.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(String userId);
}
