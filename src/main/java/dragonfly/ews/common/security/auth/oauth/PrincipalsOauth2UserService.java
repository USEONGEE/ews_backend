package dragonfly.ews.common.security.auth.oauth;

import dragonfly.ews.common.security.auth.PrincipalDetails;
import dragonfly.ews.common.security.auth.oauth.userinfo.OAuth2UserInfo;
import dragonfly.ews.common.security.auth.oauth.userinfo.UserInfoFactory;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.domain.MemberRole;
import dragonfly.ews.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrincipalsOauth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserInfoFactory userInfoFactory;

    /**
     * [OAuth 로그인]
     * <br/> 사용자가 회원가입 된 적이 없으면, 강제 회원 가입
     * @param userRequest 리소스 서버에서 보내주는 사용자 정보
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String provider = userRequest.getClientRegistration().getClientName();
        log.info("provider={}", provider);
        OAuth2UserInfo userInfo = userInfoFactory.getOAuthUserInfo(provider, attributes);

        Optional<Member> findUser = memberRepository.findByEmail(userInfo.getEmail());

        if (findUser.isEmpty()) {
            Member newMember = Member.builder()
                    .provider(userInfo.getProvider())
                    .providerId(userInfo.getProviderId())
                    .memberRole(MemberRole.ROLE_USER)
                    .email(userInfo.getEmail())
                    .password(passwordEncoder.encode(userInfo.getProvider())).build();
            memberRepository.save(newMember);
            return new PrincipalDetails(newMember, oAuth2User.getAttributes());
        }
        return new PrincipalDetails(findUser.get(), oAuth2User.getAttributes());

    }
}
