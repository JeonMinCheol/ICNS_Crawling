package icns.crawling.service;

import icns.crawling.dto.IndexResponseDTO;
import icns.crawling.dto.SearchResponseDTO;
import icns.crawling.dto.SimpleResponseDTO;
import icns.crawling.model.*;
import icns.crawling.dto.DetailedResponseDTO;
import icns.crawling.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.sql.Date;
import java.util.*;

import static java.lang.Math.max;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseService {
    private final CaseInformationRepo caseInformationRepo;
    private final PlaintiffLawyerRepo plaintiffLawyerRepo;
    private final DefendantLawyerRepo defendantLawyerRepo;
    private final DecisionKeywordRepo decisionKeywordRepo;
    private final LawyerInformationRepo lawyerInformationRepo;

    public ResponseEntity<?> responseCaseDetailedInfo(String casename, String date) throws Exception {
        Integer[] count = new Integer[] { 0, 0, 0, 0 };
        ArrayList<String> pname = new ArrayList<>(){};
        ArrayList<String> dname = new ArrayList<>(){};

        CaseInformationDTO caseDTO = caseInformationRepo
                .findByCaseNameAndDecisionDate(casename, Date.valueOf(date)).orElseThrow(Exception::new);

        int caseId = caseDTO.get_id();

        List<PlaintiffLawyerDTO> plaintiffLawyer = plaintiffLawyerRepo
                .findAllByCaseId(caseId);

        List<DefendantLawyerDTO> defendantLawyer = defendantLawyerRepo
                .findAllByCaseId(caseId);

        List<DecisionKeywordDTO> decisionKeyword = decisionKeywordRepo
                .findAllByCaseId(caseId);

        if(plaintiffLawyer.size() > 0){
            PlaintiffLawyerDTO pfirst = plaintiffLawyer.get(0);
            ArrayList<String> Name = new ArrayList<>();

            for (int i=0; i<plaintiffLawyer.size(); i++) {
                PlaintiffLawyerDTO plaintiffLawyerDTO = plaintiffLawyer.get(i);
                pname.add(plaintiffLawyerDTO.getName());
            }
            count[0] = pfirst.getWin();
            count[1] = pfirst.getLose();
        }

        if (defendantLawyer.size() > 0) {
            DefendantLawyerDTO dfirst = defendantLawyer.get(0);
            ArrayList<String> Name = new ArrayList<>();

            for (int i=0; i<defendantLawyer.size(); i++) {
                DefendantLawyerDTO defendantLawyerDTO = defendantLawyer.get(i);
                dname.add(defendantLawyerDTO.getName());
            }
            count[2] = dfirst.getWin();
            count[3] = dfirst.getLose();
        }

        List<String> keywords = new ArrayList<>();
        List<String> paragraphs = new ArrayList<>();

        for(int i=0; i < decisionKeyword.size(); i++) {
            keywords.add(decisionKeyword.get(i).getKeyword().trim());
            paragraphs.add(decisionKeyword.get(i).getParagraph().trim());
        }

        DetailedResponseDTO detailedDTO = DetailedResponseDTO.builder()
                .caseName(caseDTO.getCaseName())
                .courtName(caseDTO.getCourtName())
                .indexNo(caseDTO.getIndexNo())
                .plaintiff(caseDTO.getPlaintiff())
                .defendant(caseDTO.getDefendant())
                .plaintiffLawyerName(pname)
                .plaintiffLawyerWin(count[0])
                .plaintiffLawyerLose(count[1])
                .defendantLawyerName(dname)
                .defendantLawyerWin(count[2])
                .defendantLawyerLose(count[3])
                .keyword(keywords)
                .paragraph(paragraphs)
                .url(caseDTO.getUrl())
                .decisionDate(caseDTO.getDecisionDate())
                .build();

        return new ResponseEntity<DetailedResponseDTO>(detailedDTO, HttpStatus.OK);
    }

    public ResponseEntity<?> responseCaseSimpleInfo(String lawyer) throws Exception {
        LawyerInformationDTO lawyerDTO = lawyerInformationRepo
                .findByName(lawyer).orElseThrow(Exception::new);

        int lawyerNo = lawyerDTO.getId();
        int count = lawyerDTO.getCount();
        int caseLose = lawyerDTO.getCase_lose();
        int caseWin = lawyerDTO.getCase_win();

        List<PlaintiffLawyerDTO> allByLawyerNo = plaintiffLawyerRepo.findAllByLawyerNo(lawyerNo);
        List<DefendantLawyerDTO> allByLawyerNo1 = defendantLawyerRepo.findAllByLawyerNo(lawyerNo);

        List<Integer> caseIds = new ArrayList<>();
        for(int i = 0; i < max(allByLawyerNo.size(), allByLawyerNo1.size()); i++) {
            if (i < allByLawyerNo.size()) caseIds.add(allByLawyerNo.get(i).getCaseId());
            if (i < allByLawyerNo1.size()) caseIds.add(allByLawyerNo1.get(i).getCaseId());
        }

        List<CaseInformationDTO> c = caseInformationRepo.findAllById(caseIds);
        List<Integer> win = new ArrayList<>();
        List<Integer> lose = new ArrayList<>();
        List<String> caseName = new ArrayList<>();
        List<String> url = new ArrayList<>();
        List<String> indexNo = new ArrayList<>();
        List<Date> date = new ArrayList<>();

        for(int i=0; i<c.size(); i++) {
            CaseInformationDTO info = c.get(i);
            caseName.add(info.getCaseName());
            url.add((info.getUrl()));
            indexNo.add(info.getIndexNo());
            date.add(info.getDecisionDate());
            Optional<PlaintiffLawyerDTO> p = plaintiffLawyerRepo.findByLawyerNoAndCaseId(lawyerNo, info.get_id());
            Optional<DefendantLawyerDTO> d = defendantLawyerRepo.findByLawyerNoAndCaseId(lawyerNo, info.get_id());

            if(p.isPresent()){
                win.add(p.get().getWin());
                lose.add(p.get().getLose());
            }

            if(d.isPresent()){
                win.add(d.get().getWin());
                lose.add(d.get().getLose());
            }
        }

        SimpleResponseDTO simpleResponseDTO = SimpleResponseDTO
                .builder()
                .name(lawyer)
                .caseName(caseName)
                .date(date)
                .indexNo(indexNo)
                .win(win)
                .lose(lose)
                .url(url)
                .case_win(caseWin)
                .case_lose(caseLose)
                .count(count)
                .build();

        return new ResponseEntity<SimpleResponseDTO>(simpleResponseDTO, HttpStatus.OK);
    }

    public ResponseEntity<?> lawyerSearchResponse(String lawyer, String page) throws Exception {
        List<SearchResponseDTO> SearchResponseDTOList = new ArrayList<>();

        log.info(String.valueOf(lawyer.equals("null")));

        if(lawyer.equals("null")) {
            for (LawyerInformationDTO lawyerInformationDTO : lawyerInformationRepo.searchAll(Integer.parseInt(page))) {
                if (lawyerInformationDTO.getName().equals("Unknown"))
                    continue;

                SearchResponseDTO searchResponseDTO = SearchResponseDTO
                        .builder()
                        .name(lawyerInformationDTO.getName())
                        .win(lawyerInformationDTO.getWin())
                        .lose(lawyerInformationDTO.getLose())
                        .case_win(lawyerInformationDTO.getCase_win())
                        .case_lose(lawyerInformationDTO.getCase_lose())
                        .count(lawyerInformationDTO.getCount())
                        .build();

                SearchResponseDTOList.add(searchResponseDTO);
            }
            return new ResponseEntity<List<SearchResponseDTO>>(SearchResponseDTOList, HttpStatus.OK);
        }

        for (LawyerInformationDTO lawyerInformationDTO : lawyerInformationRepo.searchLawyerByName(lawyer, Integer.parseInt(page))) {
            if (lawyerInformationDTO.getName().equals("Unknown"))
                continue;

            SearchResponseDTO searchResponseDTO = SearchResponseDTO
                    .builder()
                    .name(lawyerInformationDTO.getName())
                    .win(lawyerInformationDTO.getWin())
                    .lose(lawyerInformationDTO.getLose())
                    .case_win(lawyerInformationDTO.getCase_win())
                    .case_lose(lawyerInformationDTO.getCase_lose())
                    .count(lawyerInformationDTO.getCount())
                    .build();

            SearchResponseDTOList.add(searchResponseDTO);
        }

        return new ResponseEntity<List<SearchResponseDTO>>(SearchResponseDTOList, HttpStatus.OK);
    }

    public ResponseEntity<?> indexSearchResponse(String indexNo, String page) throws Exception {
        if(indexNo.equals("null")) {
            List<CaseInformationDTO> caseInformationDTOList = caseInformationRepo
                    .searchAll( Integer.parseInt(page));
            return new ResponseEntity<List<CaseInformationDTO>>(caseInformationDTOList, HttpStatus.OK);
        }

        List<CaseInformationDTO> caseInformationDTOList = caseInformationRepo
                .searchByIndexNo(indexNo, Integer.parseInt(page));

        return new ResponseEntity<List<CaseInformationDTO>>(caseInformationDTOList, HttpStatus.OK);
    }

    public ResponseEntity<?> keywordSearchResponse(String keyword, String page) throws Exception {
        List<DecisionKeywordDTO> allByKeyword = null;
        if(keyword.equals("null"))
            allByKeyword = decisionKeywordRepo.searchAll(Integer.parseInt(page));
        else
            allByKeyword = decisionKeywordRepo.searchByKeyword(keyword, Integer.parseInt(page));

        List<CaseInformationDTO> caseInformationDTOList = new ArrayList<>();
        Set<Integer> caseIds = new HashSet<>();

        for (int i = 0; i < allByKeyword.size(); i++) {
            int caseId = allByKeyword.get(i).getCaseId();

            if (caseIds.contains(caseId))
                continue;
            else
                caseIds.add(caseId);

            CaseInformationDTO info = caseInformationRepo.findById(caseId).orElseThrow(Exception::new);

            CaseInformationDTO build = CaseInformationDTO.builder()
                    .caseName(info.getCaseName())
                    .url(info.getUrl())
                    .indexNo(info.getIndexNo())
                    .plaintiff(info.getPlaintiff())
                    .defendant(info.getDefendant())
                    .courtName(info.getCourtName())
                    .decisionDate(info.getDecisionDate())
                    .build();

            caseInformationDTOList.add(build);
        }

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

    public ResponseEntity<?> keywordCounting(String keyword) {
        long count = 0;
        if (keyword.equals("null")) count = decisionKeywordRepo.countAll();
        else count = decisionKeywordRepo.count(keyword);
        return new ResponseEntity<Long>(count, HttpStatus.OK);
    }
}
