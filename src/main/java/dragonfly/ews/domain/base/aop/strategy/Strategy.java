package dragonfly.ews.domain.base.aop.strategy;

import dragonfly.ews.domain.file.domain.FileExtension;

public interface Strategy {
    /**
     * [확장자를 지원하는 지에 대한 여부]
     * @param fileExtension
     * @return
     */
    boolean canSupport(FileExtension fileExtension);
}
