package dragonfly.ews.domain.file.aop.utils;

import dragonfly.ews.domain.base.aop.strategy.MemberFileStrategy;

public interface MemberFileManagerConfig {
    void setFileStrategy(MemberFileStrategy strategy) ;

    MemberFileStrategy getFileStrategy();

    void removeStrategy();

}
