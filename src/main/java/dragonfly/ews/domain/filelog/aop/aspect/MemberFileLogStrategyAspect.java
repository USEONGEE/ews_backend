package dragonfly.ews.domain.filelog.aop.aspect;

import dragonfly.ews.domain.file.aop.strategy.MemberFileStrategy;
import dragonfly.ews.domain.file.aop.utils.MemberFileManagerConfig;
import dragonfly.ews.domain.file.domain.FileExtension;
import dragonfly.ews.domain.file.utils.FileUtils;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Aspect
public class MemberFileLogStrategyAspect {
    private final MemberFileLogRepository memberFileLogRepository;
    private final MemberFileManagerConfig fileManagerConfig;
    private final FileUtils fileUtils;
    private final List<MemberFileStrategy> strategies;

    /**
     * [UseMemberFileManager와 memberFileId가 제공된 경우]
     * <p/> - 권한 처리 O
     *
     * @param joinPoint
     */
    // TODO 현재 args() 표현식이 적용이 안되어서 if문으로 해결함. 나중에 수정해야함
    @Before("@annotation(dragonfly.ews.domain.flelog.aop.annotation.UseMemberFileLogManager)")
    public void setFileStrategyForFindById(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof Long && args[0] instanceof Long) {
            MemberFileLog memberFileLog = memberFileLogRepository.findByIdAuth((Long) args[0], (Long) args[1])
                    .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

            FileExtension fileExtension = memberFileLog.getMemberFile().getFileExtension(); // 가정
            setFileStrategyBasedOnExtension(fileExtension);
        }
    }

    /**
     * [ThreadLocal 삭제]
     *
     * @param joinPoint
     */
    @After("@annotation(dragonfly.ews.domain.flelog.aop.annotation.UseMemberFileLogManager)")
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
