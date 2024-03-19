package dragonfly.ews.domain.result.service;

import dragonfly.ews.domain.file.domain.MemberFile;
import dragonfly.ews.domain.file.repository.MemberFileRepository;
import dragonfly.ews.domain.filelog.domain.MemberFileLog;
import dragonfly.ews.domain.filelog.repository.MemberFileLogRepository;
import dragonfly.ews.domain.member.domain.Member;
import dragonfly.ews.domain.member.domain.MemberRole;
import dragonfly.ews.domain.member.repository.MemberRepository;
import dragonfly.ews.domain.result.domain.AnalysisStatus;
import dragonfly.ews.domain.result.domain.AnalysisResult;
import dragonfly.ews.domain.result.postprocessor.AnalysisPostProcessor;
import dragonfly.ews.domain.result.repository.AnalysisResultRepository;
import jakarta.persistence.EntityManager;
import jdk.jfr.Description;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AnalysisResultServiceTest {
    @Value("${file.test.dir}")
    String fileDir;
    @Autowired
    MemberFileLogRepository memberFileLogRepository;
    @Autowired
    AnalysisResultRepository analysisResultRepository;
    @Autowired
    AnalysisPostProcessor analysisResultProcessor;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberFileRepository memberFileRepository;
    @Autowired
    EntityManager em;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Mock
    private WebClient webClient;


    /**
     * [흐름]
     * <br/> 1.AnalysisResultServiceImpl.createAnalysisResult() 호출
     * <br/> 2.AnalysisResultServiceImpl.analysis() 호출
     * <br/> 3.AnalysisResultProcessor.processResult() 완료 이전 상태 조회
     * <br/> 4.AnalysisResultProcessor.processResult() 완료 이후 상태 조회
     * <p/> [특이사항]
     * <br/> 1. WebClient 는 다른 스레드에서 돌기 때문에 em.flush() 뿐만 아니라 커밋까지 이어진 후에 AnalysisResultProcessor
     * 에게 ID가 전달되어야 한다. -> PlatformTransactionManager 를 이용한 트랜잭션 직접 선언
     */
    @Description("analysis() 메소드 테스트, body로는 아무것도 안 넘김")
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @Disabled
    void analysis() throws InterruptedException {
        // given
        String htmlResponse = "<html><body>Hello!</body></html>";
        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.delay(Duration.ofSeconds(3)) // 10초 지연
                        .map(delay -> htmlResponse));

        Member member = null;
        MemberFile memberFile = null;
        MemberFileLog memberFileLog = null;
        AnalysisResult analysisResult = null;
        // when

        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            transactionManager.getTransaction(def);
            member = createMember();
            memberFile = createMemberFile(member);
            memberFileLog = memberFile.getMemberFileLogs().get(0);
            analysisResult = new AnalysisResult(memberFileLog, AnalysisStatus.PROCESSING);
            analysisResultRepository.save(analysisResult);
            em.flush();
            transactionManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            transactionManager.rollback(status);
        }

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new FileSystemResource(fileDir + memberFileLog.getSavedName()));
        MultiValueMap<String, HttpEntity<?>> multipartBody = multipartBodyBuilder.build();

        String analysisUri = "http://localhost:8080/calculate";
        AnalysisResult finalAnalysisResult = analysisResult;

        System.out.println("=====WebClint 요청 보냄=====");
        webClient.post()
                .uri(analysisUri)
                .bodyValue(multipartBody)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(result ->
                        analysisResultProcessor.success((String) result, (Long) finalAnalysisResult.getId()));
        // then
        AnalysisResult findResult
                = analysisResultRepository.findById(analysisResult.getId()).get();
        assertThat(findResult.getAnalysisStatus()).isEqualTo(AnalysisStatus.PROCESSING);
        Thread.sleep(5000);
        em.clear();

        System.out.println("=====WebClint 이후 검증 시작=====");
        AnalysisResult secondFind
                = analysisResultRepository.findById(analysisResult.getId()).get();
        assertThat(secondFind.getAnalysisStatus()).isEqualTo(AnalysisStatus.COMPLETED);
    }

    Member createMember() {
        Member newMember = new Member("shdbtjd9@naver.com", 20, MemberRole.ROLE_USER);
        newMember.changePassword(passwordEncoder, "12345");
        return memberRepository.save(newMember);
    }

    MemberFile createMemberFile(Member member) {
        MemberFile memberFile = new MemberFile(member, "file.txt", "savedFile.txt");
        return memberFileRepository.save(memberFile);
    }
}