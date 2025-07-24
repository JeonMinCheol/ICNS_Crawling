package icns.crawling.controller;

import icns.crawling.service.CaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Date;

import static org.hibernate.query.sqm.tree.SqmNode.log;

// 사건 및 변호사 정보 관련 API를 처리하는 REST 컨트롤러
// URL 경로는 "/api"로 시작하며, 프론트엔드 도메인에 대해 CORS 허용

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CaseController {
    private final CaseService caseService;

    /**
     * 사건 이름 + 날짜로 사건의 상세 정보 조회
     *
     * @param casename 사건 이름
     * @param date     판결일 (yyyy-MM-dd 형식)
     * @return 사건 상세 정보 DTO 리스트 (200 OK), 실패 시 204 No Content
     */
    @GetMapping(value = "/caseinfo")
    public ResponseEntity<?> caseInfo(@RequestParam(value = "casename") String casename, @RequestParam(value = "date") String date) throws IOException {
        try{
            return caseService.responseCaseDetailedInfo(casename, date);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(204).build();
        }
    }

    /**
     * 특정 변호사의 간단한 정보 조회 (소속, 전적, 사건 목록 등)
     *
     * @param lawyer 변호사 이름
     * @return 변호사 요약 정보 DTO (200 OK), 실패 시 204 No Content
     */
    @GetMapping(value = "/lawyerinfo")
    public ResponseEntity<?> lawyerInfo(@RequestParam(value = "lawyer") String lawyer) throws IOException {
        try{
            return caseService.responseCaseSimpleInfo(lawyer);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(204).build();
        }
    }

    /**
     * 변호사 이름 기반으로 검색 (페이지네이션 포함)
     *
     * @param lawyer 검색할 변호사 이름
     * @param page   페이지 번호 (1부터 시작)
     * @return 변호사 리스트 (200 OK), 실패 시 204 No Content
     */
    @GetMapping(value = "/search/lawyer")
    public ResponseEntity<?> searchByLawyer(@RequestParam(value = "lawyer") String lawyer, @RequestParam(value="page") String page) throws IOException {
        try{
            return caseService.lawyerSearchResponse(lawyer, page);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(204).build();
        }
    }

    /**
     * 사건번호(indexNo) 기반 검색 (페이지네이션 포함)
     *
     * @param indexNo 사건번호
     * @param page    페이지 번호
     * @return 사건 정보 리스트 (200 OK), 실패 시 204 No Content
     */
    @GetMapping(value = "/search/index")
    public ResponseEntity<?> searchByIndex(@RequestParam(value = "indexNo") String indexNo, @RequestParam(value="page") String page) throws IOException {
        try{
            return caseService.indexSearchResponse(indexNo, page);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(204).build();
        }
    }

    /**
     * 변호사 이름을 기준으로 검색 결과 개수 반환
     *
     * @param lawyer 검색어 (없을 경우 "null"로 전달)
     * @return 총 개수 (200 OK), 실패 시 204 No Content
     */
    @GetMapping(value = "/count/lawyer")
    public ResponseEntity<?> lawyerCount(@RequestParam(value = "lawyer") String lawyer) {
        try{
            return caseService.lawyerCounting(lawyer);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(204).build();
        }
    }

    /**
     * 사건번호를 기준으로 검색 결과 개수 반환
     *
     * @param indexNo 사건번호 검색어
     * @return 총 개수 (200 OK), 실패 시 204 No Content
     */
    @GetMapping(value = "/count/indexNo")
    public ResponseEntity<?> indexNoCount(@RequestParam(value = "indexNo") String indexNo) {
        try{
            return caseService.indexNoCounting(indexNo);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(204).build();
        }
    }
}
