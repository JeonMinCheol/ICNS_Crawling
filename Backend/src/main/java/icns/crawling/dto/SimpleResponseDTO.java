package icns.crawling.dto;

import lombok.*;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleResponseDTO {
    private String name;
    private List<Integer> win;
    private List<Integer> lose;
    private int count;
    private int case_win;
    private int case_lose;
    private List<String> caseName;
    private List<String> url;
    private List<String> indexNo;
    private List<Date> date;
}
