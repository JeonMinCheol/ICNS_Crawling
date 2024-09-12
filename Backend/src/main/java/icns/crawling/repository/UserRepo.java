package icns.crawling.repository;

import icns.crawling.model.MemberDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<MemberDTO, Integer> {
    Optional<MemberDTO> findByEmail(String email);
}
