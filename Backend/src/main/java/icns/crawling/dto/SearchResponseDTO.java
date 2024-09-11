package icns.crawling.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponseDTO {
    private String name;
    private int case_win;
    private int case_lose;
    private int win;
    private int lose;
    private int count;
}
