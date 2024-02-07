package dragonfly.ews.common.security.handler;

import dragonfly.ews.common.security.auth.PrincipalDetails;
import dragonfly.ews.common.security.service.JwtService;
import dragonfly.ews.domain.member.domain.MemberRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [OAUth 로그인 성공시]
 * <p/> 테스트용. 추가 회원가입 설정 X
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("[OAuth2LoginSuccessHandler] OAuth2 Login 성공!");
        try {
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

            List<String> matched = principalDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority -> authority.equals(MemberRole.ROLE_USER.name()))
                    .collect(Collectors.toList());

            if (matched.isEmpty()) {
                String accessToken = jwtService.createAccessToken(principalDetails.getUsername());
                response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
                response.sendRedirect("oauth2/sign-up"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트

                jwtService.sendAccessAndRefreshToken(response, accessToken, null);
            } else {
                loginSuccess(response, principalDetails); // 로그인에 성공한 경우 access, refresh 토큰 생성
            }
        } catch (Exception e) {
            throw e;
        }
    }

    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response, PrincipalDetails principalDetails) throws IOException {
        String accessToken = jwtService.createAccessToken(principalDetails.getUsername());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(principalDetails.getUsername(), refreshToken);
        log.info("[OAuth2LoginSuccessHandler] OAuth 접근 토큰값={}", accessToken);
    }
}
