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
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CaseController {
    private final CaseService caseService;

    @GetMapping(value = "/caseinfo")
    public ResponseEntity<?> caseInfo(@RequestParam(value = "casename") String casename, @RequestParam(value = "date") String date) throws IOException {
        try{
            return caseService.responseCaseDetailedInfo(casename, date);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(204).build();
        }
    }

    @GetMapping(value = "/lawyerinfo")
    public ResponseEntity<?> lawyerInfo(@RequestParam(value = "lawyer") String lawyer) throws IOException {
        try{
            return caseService.responseCaseSimpleInfo(lawyer);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(204).build();
        }
    }

    @GetMapping(value = "/search/lawyer")
    public ResponseEntity<?> searchByLawyer(@RequestParam(value = "lawyer") String lawyer, @RequestParam(value="page") String page) throws IOException {
        try{
            log.info("lawyer", lawyer);
            return caseService.lawyerSearchResponse(lawyer, page);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(204).build();
        }
    }

    @GetMapping(value = "/search/index")
    public ResponseEntity<?> searchByIndex(@RequestParam(value = "indexNo") String indexNo, @RequestParam(value="page") String page) throws IOException {
        try{
            return caseService.indexSearchResponse(indexNo, page);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(204).build();
        }
    }

    @GetMapping(value = "/search/keyword")
    public ResponseEntity<?> searchByKeyword(@RequestParam(value = "keyword") String keyword, @RequestParam(value="page") String page) throws IOException {
        try{
            return caseService.keywordSearchResponse(keyword, page);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(204).build();
        }
    }

    @GetMapping(value = "/count/lawyer")
    public ResponseEntity<?> lawyerCount(@RequestParam(value = "lawyer") String lawyer) {
        try{
            return caseService.lawyerCounting(lawyer);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(204).build();
        }
    }

    @GetMapping(value = "/count/indexNo")
    public ResponseEntity<?> indexNoCount(@RequestParam(value = "indexNo") String indexNo) {
        try{
            return caseService.indexNoCounting(indexNo);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(204).build();
        }
    }

    @GetMapping(value = "/count/keyword")
    public ResponseEntity<?> keywordCount(@RequestParam(value = "keyword") String keyword) {
        try{
            return caseService.keywordCounting(keyword);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(204).build();
        }
    }
}
