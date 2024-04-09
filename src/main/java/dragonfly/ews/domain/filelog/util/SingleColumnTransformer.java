package dragonfly.ews.domain.filelog.util;

import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.filelog.domain.SingleColumnTransformMethod;
import dragonfly.ews.domain.filelog.dto.SingleColumnTransformRequestDto;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * [엑셀 데이터를 transform]
 * <br/> 임시로 사용. 나중에는 외부 서버로 해당 프로세스를 빼야함
 */
public interface SingleColumnTransformer {

    boolean canSupport(FileExtension fileExtension);

    void transform(SingleColumnTransformRequestDto dto) throws IOException;
}
