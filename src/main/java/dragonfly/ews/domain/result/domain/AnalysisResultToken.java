package dragonfly.ews.domain.result.domain;


import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("analysisResultToken")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class AnalysisResultToken {
    @Id
    private Long id;
    private String token;
    
    @TimeToLive // 유지되는 시간
    private Long expiration; // TTL 값

    /**
     * [실제 redis에 저장되는 key값]
     * @return
     */
    public String getRedisKey() {
        return "analysisResultToken:" + this.id;
    }
}