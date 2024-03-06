package dragonfly.ews.domain.file.aop.aspect;

import dragonfly.ews.domain.file.FileUtils;
import dragonfly.ews.domain.file.aop.utils.MemberFileManagerConfig;
import dragonfly.ews.domain.file.aop.strategy.CsvStrategy;
import dragonfly.ews.domain.file.aop.strategy.XlsxStrategy;
import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.dto.MemberFileCreateDto;
import dragonfly.ews.domain.file.dto.MemberFileUpdateDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static dragonfly.ews.domain.file.domain.FileExtension.XLS;

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
    private final CsvStrategy csvStrategy;
    private final XlsxStrategy xlsxStrategy;

    /**
     * [UseMemberFileManager, HasMultipartFile 이 붙은 메소드에 대한 AOP]
     * <p/> 메소드의 파라미터에 직접적으로 혹은 DTO가 MultipartFile을 가진 경우에 사용
     * @param joinPoint
     */
    @Before("@annotation(dragonfly.ews.domain.file.aop.annotation.UseMemberFileManager) && " +
            "@annotation(dragonfly.ews.domain.file.aop.annotation.HasMultipartFile)")
    public void setFileStrategyBasedOnExtension(JoinPoint joinPoint) {
        MultipartFile multipartFile = null;
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof MemberFileCreateDto) {
                multipartFile = ((MemberFileCreateDto) arg).getFile();
            } else if (arg instanceof MemberFileUpdateDto) {
                multipartFile = ((MemberFileUpdateDto) arg).getFile();
            } else if (arg instanceof MultipartFile) {
                multipartFile = (MultipartFile) arg;
            }
        }
        if (multipartFile != null) {
            setFileStrategyBasedOnExtension(multipartFile);
        }
    }

    /**
     * [ThreadLocal 삭제]
     * @param joinPoint
     */
    @After("@annotation(dragonfly.ews.domain.file.aop.annotation.UseMemberFileManager)")
    public void removeStrategy(JoinPoint joinPoint) {
        fileManagerConfig.removeStrategy();
    }


    public void setFileStrategyBasedOnExtension(MultipartFile file) {

        String fileName = file.getOriginalFilename();
        FileExtension fileExtension = FileExtension.fromString(fileUtils.getFileExt(fileName));

        switch (fileExtension) {
            case CSV:
                fileManagerConfig.setFileStrategy(csvStrategy);
                break;
            case XLSX, XLS:
                fileManagerConfig.setFileStrategy(xlsxStrategy);
                break;
            default:
                throw new IllegalArgumentException("Unsupported file format");
        }
    }
}