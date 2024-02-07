package dragonfly.ews.common.security.auth.oauth.userinfo;


import java.util.Map;

public class GoogleOAuthUserInfo implements OAuth2UserInfo {
    String providerId;
    String provider;
    String username;
    String email;

    public GoogleOAuthUserInfo(Map<String, Object> attr) {
        this.providerId = (String) attr.get("sub");
        this.email = (String) attr.get("email");
        this.provider = "GOOGLE";
        this.username = this.provider + "_" + this.providerId;
    }

    @Override
    public String getProviderId() {
        return this.providerId;
    }

    @Override
    public String getProvider() {
        return this.provider;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
