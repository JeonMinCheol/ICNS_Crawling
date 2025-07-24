package icns.crawling.repository;

import icns.crawling.model.MemberDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<MemberDTO, Integer> {
    /**
     * 이메일을 기준으로 사용자 정보를 조회
     *
     * @param email 사용자의 이메일
     * @return 해당 이메일을 가진 사용자 정보 (Optional로 반환, 없으면 empty)
     */
    Optional<MemberDTO> findByEmail(String email);
}
