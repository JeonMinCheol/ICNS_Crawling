package icns.crawling.repository;

import icns.crawling.model.DefendantLawyerDTO;
import icns.crawling.model.PlaintiffLawyerDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DefendantLawyerRepo extends JpaRepository<DefendantLawyerDTO, Integer> {
    List<DefendantLawyerDTO> findAllByCaseId(int id);
    List<DefendantLawyerDTO> findAllByLawyerNoOrderById(int lawyerNo);
    List<DefendantLawyerDTO> findAllByLawyerNo(int lawyerNo);
    Optional<DefendantLawyerDTO> findByLawyerNoAndCaseId(int lawyerNo, int CaseId);
}
