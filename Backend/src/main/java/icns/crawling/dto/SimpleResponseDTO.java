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
    private int win;
    private int lose;
    private int count;
    private List<String> caseName;
    private List<String> indexNo;
    private List<Date> date;
}
