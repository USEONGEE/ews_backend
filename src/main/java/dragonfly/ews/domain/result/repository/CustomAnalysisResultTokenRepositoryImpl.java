package dragonfly.ews.domain.result.repository;

import dragonfly.ews.domain.result.domain.AnalysisResultToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

@RequiredArgsConstructor
public class CustomAnalysisResultTokenRepositoryImpl implements CustomAnalysisResultTokenRepository {

    private final RedisTemplate<String, AnalysisResultToken> redisTemplate;

    @Override
    public Optional<AnalysisResultToken> findByRedisKey(String redisKey) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(redisKey));
    }
}
