package dragonfly.ews.domain.result.repository;

import dragonfly.ews.domain.result.domain.AnalysisResultToken;

import java.util.Optional;

public interface CustomAnalysisResultTokenRepository {
    Optional<AnalysisResultToken>  findByRedisKey(String redisKey);
}
