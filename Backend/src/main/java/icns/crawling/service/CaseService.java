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

    /**
     * 상세 사건 정보 조회
     * 사건 이름과 날짜를 기준으로 사건 관련 정보 및 판결문, 변호인 정보 등을 반환
     */
    public ResponseEntity<?> responseCaseDetailedInfo(String casename, String date) throws Exception {
        ArrayList<String> pname = new ArrayList<>(){};
        ArrayList<String> dname = new ArrayList<>(){};

        // 사건 이름과 판결일로 사건 정보 검색
        List<CaseInformationDTO> caseInfos = caseInformationRepo
                .findByCaseNameAndDecisionDate(casename, Date.valueOf(date));

        List<DetailedResponseDTO> responseDTOS = new ArrayList<>();

        for(int infoIdx=0; infoIdx < caseInfos.size(); infoIdx++) {
            CaseInformationDTO caseDTO = caseInfos.get(infoIdx);
            int caseId = caseDTO.get_id();

            // 원고/피고 변호사, 판결문 정보 조회
            List<PlaintiffLawyerDTO> plaintiffLawyer = plaintiffLawyerRepo
                    .findAllByCaseId(caseId);

            List<DefendantLawyerDTO> defendantLawyer = defendantLawyerRepo
                    .findAllByCaseId(caseId);

            List<CaseDecisionDTO> caseDecisionDTOS = caseDecisionRepo
                    .findAllByCaseId(caseId);

            // 변호사 이름 수집
            if(plaintiffLawyer.size() > 0){
                for (int i=0; i<plaintiffLawyer.size(); i++) {
                    PlaintiffLawyerDTO plaintiffLawyerDTO = plaintiffLawyer.get(i);
                    pname.add(plaintiffLawyerDTO.getName());
                }
            }

            // 변호사 이름 수집
            if (defendantLawyer.size() > 0) {
                for (int i=0; i<defendantLawyer.size(); i++) {
                    DefendantLawyerDTO defendantLawyerDTO = defendantLawyer.get(i);
                    dname.add(defendantLawyerDTO.getName());
                }
            }

            List<String> sentences = new ArrayList<>();
            List<String> paragraphs = new ArrayList<>();

            // 판결문 정보 수집
            for(int i=0; i < caseDecisionDTOS.size(); i++) {
                sentences.add(caseDecisionDTOS.get(i).getSentence().trim());
                paragraphs.add(caseDecisionDTOS.get(i).getParagraph().trim());
            }

            // 상세 정보 DTO 구성
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

            responseDTOS.add(detailedDTO);
        }

        return new ResponseEntity<List<DetailedResponseDTO>>(responseDTOS, HttpStatus.OK);
    }

    /**
     * 특정 변호사에 대한 간단 요약 정보 제공
     * 소속 로펌, 승소/패소 수, 처리 사건 목록 등 포함
     */
    public ResponseEntity<?> responseCaseSimpleInfo(String lawyer) throws Exception {
        LawyerInformationDTO lawyerDTO = lawyerInformationRepo
                .findByName(lawyer).orElseThrow(Exception::new);

        int lawyerNo = lawyerDTO.getId();
        int count = lawyerDTO.getCount();
        int caseLose = lawyerDTO.getLose();
        int caseWin = lawyerDTO.getWin();
        String lawfirm = lawyerDTO.getLawfirm();

        // 변호사 참여 사건 ID 수집
        List<PlaintiffLawyerDTO> allByLawyerNo = plaintiffLawyerRepo.findAllByLawyerNo(lawyerNo);
        List<DefendantLawyerDTO> allByLawyerNo1 = defendantLawyerRepo.findAllByLawyerNo(lawyerNo);

        List<Integer> caseIds = new ArrayList<>();
        for(int i = 0; i < max(allByLawyerNo.size(), allByLawyerNo1.size()); i++) {
            if (i < allByLawyerNo.size()) caseIds.add(allByLawyerNo.get(i).getCaseId());
            if (i < allByLawyerNo1.size()) caseIds.add(allByLawyerNo1.get(i).getCaseId());
        }

        // 사건 정보 조회
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

        // 응답 DTO 구성
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

    /**
     * 변호사 이름 기반 검색 결과 반환 (페이지네이션 지원)
     */
    public ResponseEntity<?> lawyerSearchResponse(String lawyer, String page) throws Exception {
        List<SearchResponseDTO> SearchResponseDTOList = new ArrayList<>();

        log.info(String.valueOf(lawyer.equals("null")));
        int pageSize = 50;

        // 전체 변호사 조회

        // 검색어가 없는 경우
        if(lawyer.equals("null")) {
            for (LawyerInformationDTO lawyerInformationDTO : lawyerInformationRepo.searchAll(pageSize * (Integer.parseInt(page) - 1))) {
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
        for (LawyerInformationDTO lawyerInformationDTO : lawyerInformationRepo.searchLawyerByName(lawyer, pageSize * (Integer.parseInt(page) - 1))) {
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

    /**
     * 사건번호 기반 검색 결과 반환 (페이지네이션 지원)
     */
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

    /**
     * 변호사 수 카운트 반환 (검색어에 따라 전체 또는 부분 집계)
     */
    public ResponseEntity<?> lawyerCounting(String lawyer) {
        long count = 0;
        if (lawyer.equals("null")) count = lawyerInformationRepo.countAll();
        else count = lawyerInformationRepo.count(lawyer);
        return new ResponseEntity<Long>(count, HttpStatus.OK);
    }

    /**
     * 사건번호 수 카운트 반환 (검색어에 따라 전체 또는 부분 집계)
     */
    public ResponseEntity<?> indexNoCounting(String indexNo) {
        long count = 0;
        if (indexNo.equals("null")) count = caseInformationRepo.countAll();
        else count = caseInformationRepo.count(indexNo);
        return new ResponseEntity<Long>(count, HttpStatus.OK);
    }
}
