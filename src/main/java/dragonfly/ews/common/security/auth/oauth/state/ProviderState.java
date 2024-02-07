package dragonfly.ews.common.security.auth.oauth.state;

import dragonfly.ews.common.security.auth.oauth.userinfo.OAuth2UserInfo;

import java.util.Map;

public interface ProviderState {
    boolean canSupport(String provider);

    OAuth2UserInfo getUserInfo(Map<String ,Object> attr);
}
