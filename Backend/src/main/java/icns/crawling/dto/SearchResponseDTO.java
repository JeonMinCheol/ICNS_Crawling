package icns.crawling.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponseDTO {
    private String name;
    private int win;
    private int lose;
    private int count;
    private String lawfirm;
}
