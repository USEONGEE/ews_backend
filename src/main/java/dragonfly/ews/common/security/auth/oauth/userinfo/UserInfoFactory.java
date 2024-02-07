package dragonfly.ews.common.security.auth.oauth.userinfo;

import com.google.common.annotations.VisibleForTesting;
import dragonfly.ews.common.security.auth.oauth.exception.UnSupportedOAuthException;
import dragonfly.ews.common.security.auth.oauth.state.ProviderState;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserInfoFactory {
    private final List<ProviderState> providerStates;

    public OAuth2UserInfo getOAuthUserInfo(String provider, Map<String, Object> attr) {
        for (ProviderState providerState : providerStates) {
            if (providerState.canSupport(provider)) {
                log.info("canSupport");
                return providerState.getUserInfo(attr);
            }
        }

        throw new UnSupportedOAuthException("지원하지 않는 타입의 OAuth입니다: " + provider);
    }

    @VisibleForTesting
    @PostConstruct
    public void postConstruct() {
        log.info("===== UserInfoFactory.provderStates =====");
        for (ProviderState providerState : providerStates) {
            log.info(providerState.getClass().toString());
        }
        log.info("=========================================");
    }
}
