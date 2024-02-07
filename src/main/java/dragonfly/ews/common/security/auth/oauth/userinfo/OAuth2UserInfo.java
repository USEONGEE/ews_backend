package dragonfly.ews.common.security.auth.oauth.userinfo;

/**
 * [OAuth 로그인 사용자의 정보 인터페이스]
 * <br/> OAuth로 로그인한 사용자의 정보를 담는다.
 */
public interface OAuth2UserInfo {
    String getProviderId();

    String getProvider();

    String getEmail();

    String getUsername();

}
