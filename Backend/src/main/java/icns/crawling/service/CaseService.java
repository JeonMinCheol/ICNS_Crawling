package icns.crawling.service;

import icns.crawling.dto.SearchResponseDTO;
import icns.crawling.dto.SimpleResponseDTO;
import icns.crawling.model.*;
import icns.crawling.dto.DetailedResponseDTO;
import icns.crawling.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Math.max;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseService {
    private final CaseInformationRepo caseInformationRepo;
    private final PlaintiffLawyerRepo plaintiffLawyerRepo;
    private final DefendantLawyerRepo defendantLawyerRepo;
    private final CaseDecisionRepo caseDecisionRepo;
    private final LawyerInformationRepo lawyerInformationRepo;

    public ResponseEntity<?> responseCaseDetailedInfo(String casename, String date) throws Exception {
        ArrayList<String> pname = new ArrayList<>(){};
        ArrayList<String> dname = new ArrayList<>(){};

        CaseInformationDTO caseDTO = caseInformationRepo
                .findByCaseNameAndDecisionDate(casename, Date.valueOf(date)).orElseThrow(Exception::new);

        int caseId = caseDTO.get_id();

        List<PlaintiffLawyerDTO> plaintiffLawyer = plaintiffLawyerRepo
                .findAllByCaseId(caseId);

        List<DefendantLawyerDTO> defendantLawyer = defendantLawyerRepo
                .findAllByCaseId(caseId);

        List<CaseDecisionDTO> caseDecisionDTOS = caseDecisionRepo
                .findAllByCaseId(caseId);

        if(plaintiffLawyer.size() > 0){
            for (int i=0; i<plaintiffLawyer.size(); i++) {
                PlaintiffLawyerDTO plaintiffLawyerDTO = plaintiffLawyer.get(i);
                pname.add(plaintiffLawyerDTO.getName());
            }
        }

        if (defendantLawyer.size() > 0) {
            for (int i=0; i<defendantLawyer.size(); i++) {
                DefendantLawyerDTO defendantLawyerDTO = defendantLawyer.get(i);
                dname.add(defendantLawyerDTO.getName());
            }
        }

        List<String> sentences = new ArrayList<>();
        List<String> paragraphs = new ArrayList<>();

        for(int i=0; i < caseDecisionDTOS.size(); i++) {
            sentences.add(caseDecisionDTOS.get(i).getSentence().trim());
            paragraphs.add(caseDecisionDTOS.get(i).getParagraph().trim());
        }

        DetailedResponseDTO detailedDTO = DetailedResponseDTO.builder()
                .caseName(caseDTO.getCaseName())
                .courtName(caseDTO.getCourtName())
                .indexNo(caseDTO.getIndexNo())
                .plaintiff(caseDTO.getPlaintiff())
                .defendant(caseDTO.getDefendant())
                .incidentReason(caseDTO.getIncidentReason())
                .slipOp(caseDTO.getSlipOp())
                .summary(caseDTO.getSummary())
                .plaintiffLawyerName(pname)
                .defendantLawyerName(dname)
                .plaintiffLawyerNum(pname.size())
                .defendantLawyerNum(dname.size())
                .sentences(sentences)
                .paragraphs(paragraphs)
                .decisionDate(caseDTO.getDecisionDate())
                .caseKind(caseDTO.getCaseKind())
                .judgeName(caseDTO.getJudgeName())
                .result(caseDTO.getResult())
                .build();

        return new ResponseEntity<DetailedResponseDTO>(detailedDTO, HttpStatus.OK);
    }

    public ResponseEntity<?> responseCaseSimpleInfo(String lawyer) throws Exception {
        LawyerInformationDTO lawyerDTO = lawyerInformationRepo
                .findByName(lawyer).orElseThrow(Exception::new);

        int lawyerNo = lawyerDTO.getId();
        int count = lawyerDTO.getCount();
        int caseLose = lawyerDTO.getLose();
        int caseWin = lawyerDTO.getWin();
        String lawfirm = lawyerDTO.getLawfirm();

        List<PlaintiffLawyerDTO> allByLawyerNo = plaintiffLawyerRepo.findAllByLawyerNo(lawyerNo);
        List<DefendantLawyerDTO> allByLawyerNo1 = defendantLawyerRepo.findAllByLawyerNo(lawyerNo);

        List<Integer> caseIds = new ArrayList<>();
        for(int i = 0; i < max(allByLawyerNo.size(), allByLawyerNo1.size()); i++) {
            if (i < allByLawyerNo.size()) caseIds.add(allByLawyerNo.get(i).getCaseId());
            if (i < allByLawyerNo1.size()) caseIds.add(allByLawyerNo1.get(i).getCaseId());
        }

        List<CaseInformationDTO> c = caseInformationRepo.findAllById(caseIds);
        List<String> caseNames = new ArrayList<>();
        List<String> indexNos = new ArrayList<>();
        List<String> summarys = new ArrayList<>();
        List<String> courtNames = new ArrayList<>();
        List<String> caseKinds = new ArrayList<>();
        List<Date> dates = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateFilter = LocalDate.parse("1999-01-01", formatter);

        for(int i=0; i<c.size(); i++) {
            CaseInformationDTO info = c.get(i);
            if (info.getDecisionDate().toLocalDate().isBefore(dateFilter)) continue;

            caseNames.add(info.getCaseName());
            indexNos.add(info.getIndexNo());
            dates.add(info.getDecisionDate());
            summarys.add(info.getSummary());
            caseKinds.add(info.getCaseKind());
            courtNames.add(info.getCourtName());
        }

        SimpleResponseDTO simpleResponseDTO = SimpleResponseDTO
                .builder()
                .name(lawyer)
                .caseName(caseNames)
                .date(dates)
                .indexNo(indexNos)
                .win(caseWin)
                .lose(caseLose)
                .count(count)
                .lawfirm(lawfirm)
                .build();

        return new ResponseEntity<SimpleResponseDTO>(simpleResponseDTO, HttpStatus.OK);
    }

    public ResponseEntity<?> lawyerSearchResponse(String lawyer, String page) throws Exception {
        List<SearchResponseDTO> SearchResponseDTOList = new ArrayList<>();

        log.info(String.valueOf(lawyer.equals("null")));
        int pageSize = 50;

        // 검색어가 없는 경우
        if(lawyer.equals("null")) {
            for (LawyerInformationDTO lawyerInformationDTO : lawyerInformationRepo.searchAll(pageSize * Integer.parseInt(page))) {
                SearchResponseDTO searchResponseDTO = SearchResponseDTO
                        .builder()
                        .name(lawyerInformationDTO.getName())
                        .win(lawyerInformationDTO.getWin())
                        .lose(lawyerInformationDTO.getLose())
                        .win(lawyerInformationDTO.getWin())
                        .lawfirm(lawyerInformationDTO.getLawfirm())
                        .count(lawyerInformationDTO.getCount())
                        .build();

                SearchResponseDTOList.add(searchResponseDTO);
            }
            return new ResponseEntity<List<SearchResponseDTO>>(SearchResponseDTOList, HttpStatus.OK);
        }

        // 검색어가 있는 경우
        for (LawyerInformationDTO lawyerInformationDTO : lawyerInformationRepo.searchLawyerByName(lawyer, pageSize * Integer.parseInt(page))) {
            if (lawyerInformationDTO.getName().equals("Unknown"))
                continue;

            SearchResponseDTO searchResponseDTO = SearchResponseDTO
                    .builder()
                    .name(lawyerInformationDTO.getName())
                    .win(lawyerInformationDTO.getWin())
                    .lose(lawyerInformationDTO.getLose())
                    .win(lawyerInformationDTO.getWin())
                    .lawfirm(lawyerInformationDTO.getLawfirm())
                    .count(lawyerInformationDTO.getCount())
                    .build();

            SearchResponseDTOList.add(searchResponseDTO);
        }

        return new ResponseEntity<List<SearchResponseDTO>>(SearchResponseDTOList, HttpStatus.OK);
    }

    public ResponseEntity<?> indexSearchResponse(String indexNo, String page) throws Exception {
        // 검색어가 없는 경우
        if(indexNo.equals("null")) {
            List<CaseInformationDTO> caseInformationDTOList = caseInformationRepo
                    .searchAll(Integer.parseInt(page), 50);
            return new ResponseEntity<List<CaseInformationDTO>>(caseInformationDTOList, HttpStatus.OK);
        }

        // 검색어가 있는 경우
        List<CaseInformationDTO> caseInformationDTOList = caseInformationRepo
                .searchByIndexNo(indexNo, Integer.parseInt(page), 50);

        return new ResponseEntity<List<CaseInformationDTO>>(caseInformationDTOList, HttpStatus.OK);
    }

    public ResponseEntity<?> lawyerCounting(String lawyer) {
        long count = 0;
        if (lawyer.equals("null")) count = lawyerInformationRepo.countAll();
        else count = lawyerInformationRepo.count(lawyer);
        return new ResponseEntity<Long>(count, HttpStatus.OK);
    }

    public ResponseEntity<?> indexNoCounting(String indexNo) {
        long count = 0;
        if (indexNo.equals("null")) count = caseInformationRepo.countAll();
        else count = caseInformationRepo.count(indexNo);
        return new ResponseEntity<Long>(count, HttpStatus.OK);
    }
}
