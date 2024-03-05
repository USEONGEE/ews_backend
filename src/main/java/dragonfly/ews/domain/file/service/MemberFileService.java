package dragonfly.ews.domain.file.service;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.MemberFileCreateDto;
import dragonfly.ews.domain.file.dto.MemberFileUpdateDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MemberFileService {
    /**
     * [특정 프로젝트에 파일 저장]
     * <p/> 프로젝트에 파일을 저장. ProjectId가 제공되지 않으면 파일을 저장할 수 없음
     * <br/> 프로젝트 내에 같은 파일의 존재 여부를 확인해야함. -> 존재시 업데이트
     * <br/>
     *
     * @param file
     * @param memberId
     */

    boolean saveFile(Long memberId, MemberFileCreateDto memberFileCreateDto);

    /**
     * []
     * @param file
     * @param memberId
     * @param fileId
     */

    @Deprecated
    boolean updateFile(MultipartFile file, Long memberId, Long fileId);

    boolean updateFile(Long memberId, MemberFileUpdateDto memberFileUpdateDto);

    MemberFile findByIdContainLogs(Long memberId, Long memberFileId);

    List<MemberFile> findAll(Long memberId);


    /**
     * 파일 비교를 위한 메소드도 구현해야함
     */
//    void compareFile(Long fileId1, Long fileId2);
}
