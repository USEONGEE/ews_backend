package dragonfly.ews.domain.file.aop.aspect;

import dragonfly.ews.domain.file.FileUtils;
import dragonfly.ews.domain.file.aop.strategy.MemberFileStrategy;
import dragonfly.ews.domain.file.aop.utils.MemberFileManagerConfig;
import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.dto.MemberFileCreateDto;
import dragonfly.ews.domain.file.dto.MemberFileUpdateDto;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * [UseMemberFileManger 어노테이션이 붙은 메소드에 대한 AOP를 적용하는 클래스]
 * <p/> UserMemberFileManager은 {@link dragonfly.ews.domain.file.aop.strategy.MemberFileStrategy} 를 사용한다.
 */

@Aspect
@Component
@RequiredArgsConstructor
public class MemberFileStrategyAspect {
    private final MemberFileManagerConfig fileManagerConfig;
    private final FileUtils fileUtils;
    private final MemberFileRepository memberFileRepository;
    private final List<MemberFileStrategy> strategies;

    /**
     * [UseMemberFileManager, HasMultipartFile 이 붙은 메소드에 대한 AOP]
     * <p/> 메소드의 파라미터에 직접적으로 혹은 DTO가 MultipartFile을 가진 경우에 사용
     *
     * @param joinPoint
     */
    @Before("@annotation(dragonfly.ews.domain.file.aop.annotation.UseMemberFileManager) && " +
            "@annotation(dragonfly.ews.domain.file.aop.annotation.HasMultipartFile)")
    public void setFileStrategyBasedOnExtension(JoinPoint joinPoint) {
        MultipartFile multipartFile = null;
        for (Object arg : joinPoint.getArgs()) {
            // DTO가 추가되면 추가해야함
            if (arg instanceof MemberFileCreateDto) {
                multipartFile = ((MemberFileCreateDto) arg).getFile();
            } else if (arg instanceof MemberFileUpdateDto) {
                multipartFile = ((MemberFileUpdateDto) arg).getFile();
            } else if (arg instanceof MultipartFile) {
                multipartFile = (MultipartFile) arg;
            }
        }
        if (multipartFile == null) {
            throw new IllegalArgumentException("[MemberFileStrategyAspect] 파일에 확장자가 존재하지 않습니다.");
        } else {
            FileExtension fileExtension = FileExtension.fromString(
                    fileUtils.getFileExt(multipartFile.getOriginalFilename()));
            setFileStrategyBasedOnExtension(fileExtension);
        }
    }

    /**
     * [UseMemberFileManager와 memberFileId가 제공된 경우]
     * <p/> - 권한 처리 O
     *
     * @param joinPoint
     */
    // TODO 현재 args() 표현식이 적용이 안되어서 if문으로 해결함. 나중에 수정해야함
    @Before("@annotation(dragonfly.ews.domain.file.aop.annotation.UseMemberFileManager)")
    public void setFileStrategyForFindById(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof Long && args[0] instanceof Long) {
            MemberFile memberFile = memberFileRepository.findByIdAuth((Long) args[0], (Long) args[1])
                    .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

            FileExtension fileExtension = memberFile.getFileExtension(); // 가정
            setFileStrategyBasedOnExtension(fileExtension);
        }
    }

    /**
     * [ThreadLocal 삭제]
     *
     * @param joinPoint
     */
    @After("@annotation(dragonfly.ews.domain.file.aop.annotation.UseMemberFileManager)")
    public void removeStrategy(JoinPoint joinPoint) {
        fileManagerConfig.removeStrategy();
    }


    private void setFileStrategyBasedOnExtension(FileExtension fileExtension) {
        for (MemberFileStrategy strategy : strategies) {
            if (strategy.canSupport(fileExtension)) {
                fileManagerConfig.setFileStrategy(strategy);
                return;
            }
        }
        throw new IllegalArgumentException("[MemberFileStrategyAspect] 지원하지 않는 파일 형식입니다.");
    }
}