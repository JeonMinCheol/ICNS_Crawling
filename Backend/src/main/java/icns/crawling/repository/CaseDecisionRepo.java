package icns.crawling.repository;

import icns.crawling.model.CaseDecisionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CaseDecisionRepo extends JpaRepository<CaseDecisionDTO, Integer> {
    List<CaseDecisionDTO> findAllByCaseId(int id);
    long count();

}
