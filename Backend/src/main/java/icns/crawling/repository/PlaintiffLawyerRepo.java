package icns.crawling.repository;

import icns.crawling.model.PlaintiffLawyerDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaintiffLawyerRepo extends JpaRepository<PlaintiffLawyerDTO, Integer> {
    List<PlaintiffLawyerDTO> findAllByCaseId(int id);
    List<PlaintiffLawyerDTO> findAllByLawyerNoOrderById(int lawyerNo);
    List<PlaintiffLawyerDTO> findAllByLawyerNo(int lawyerNo);
    Optional<PlaintiffLawyerDTO> findByLawyerNoAndCaseId(int lawyerNo, int CaseId);
}
