package dragonfly.ews.domain.filelog.dto;

import dragonfly.ews.domain.filelog.domain.SingleColumnTransformMethod;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [User의 DataTransformation Reqeust를 변환하는 Dto]
 */
@Data
@NoArgsConstructor
public class SingleColumnTransformRequestDto {
    Long memberFileLogId;
    Long columnId;
    SingleColumnTransformMethod method;
    String description;
}
