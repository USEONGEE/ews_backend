package dragonfly.ews.domain.result.repository;

import dragonfly.ews.domain.result.domain.AnalysisResultToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisResultTokenRepository extends CrudRepository<AnalysisResultToken, Long>, CustomAnalysisResultTokenRepository{

}
