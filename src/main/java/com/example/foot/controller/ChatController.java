package com.example.foot.controller;

import com.example.foot.constant.Role;
import com.example.foot.dto.ChatMessage;
import com.example.foot.dto.SessionUser;
import com.example.foot.entity.Member;
import com.example.foot.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Map;

@Controller  // 이 클래스가 Spring MVC의 컨트롤러임을 나타냅니다.
@RequiredArgsConstructor  // 생성자를 자동으로 만들어주는 Lombok 어노테이션으로, final 필드를 인자로 받는 생성자를 생성합니다.
@RequestMapping("/chat")  // 이 컨트롤러에서 "/chat" 경로와 관련된 요청을 처리함을 나타냅니다.
public class ChatController {

    private final MemberRepository memberRepository;  // DB에서 회원 정보를 가져오기 위한 리포지토리
    private final HttpSession httpSession;  // 현재 사용자의 세션 정보를 관리하는 HttpSession 객체

    @MessageMapping("/chat")  // "/chat" 경로로 들어오는 웹소켓 메시지를 처리
    @SendTo("/topic/messages")  // 메시지를 "/topic/messages" 경로로 송신 (브로드캐스트)
    public ChatMessage send(ChatMessage message, Principal principal) {
        String senderName = "첫손님";  // 기본 송신자 이름을 "첫손님"으로 설정

        if (principal != null) {  // 사용자가 로그인되어 있는 경우
            if (principal instanceof OAuth2AuthenticationToken) {  // 소셜 로그인 사용자라면
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;  // OAuth2 토큰 정보를 가져옴
                OAuth2User oauthUser = oauthToken.getPrincipal();  // 소셜 로그인 사용자 정보를 가져옴
                String provider = oauthToken.getAuthorizedClientRegistrationId();  // 소셜 로그인 제공자 이름 (kakao, naver, google 등)

                String email = null;  // 이메일 초기화
                String name = null;  // 이름 초기화
                System.out.println("소셜 로그인 이메일"+email);  // 이메일 로그 출력 (여기선 초기화 상태이므로 null)

                // 소셜 로그인 제공자에 따른 사용자 정보 처리
                if ("kakao".equals(provider)) {  // 카카오 로그인 사용자일 경우
                    Map<String, Object> kakaoAccount = (Map<String, Object>) oauthUser.getAttribute("kakao_account");  // 카카오 계정 정보 추출
                    email = (String) kakaoAccount.get("email");  // 카카오 계정에서 이메일 정보 추출
                    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");  // 프로필 정보 추출
                    name = (String) profile.get("nickname");  // 프로필에서 닉네임 추출
                    System.out.println("카카오 로그인 - 이메일: " + email + ", 닉네임: " + name);  // 로그로 카카오 이메일과 닉네임 출력

                } else if ("naver".equals(provider)) {  // 네이버 로그인 사용자일 경우
                    Map<String, Object> response = (Map<String, Object>) oauthUser.getAttribute("response");  // 네이버 계정의 응답 데이터 추출
                    email = (String) response.get("email");  // 네이버 응답에서 이메일 추출
                    name = (String) response.get("name");  // 네이버 응답에서 이름 추출
                    System.out.println("네이버 로그인 - 이메일: " + email + ", 이름: " + name);  // 로그로 네이버 이메일과 이름 출력

                } else if ("google".equals(provider)) {  // 구글 로그인 사용자일 경우
                    email = oauthUser.getAttribute("email");  // 구글 계정에서 이메일 추출
                    name = oauthUser.getAttribute("name");  // 구글 계정에서 이름 추출
                    System.out.println("구글 로그인 - 이메일: " + email + ", 이름: " + name);  // 로그로 구글 이메일과 이름 출력
                }

                Member member = memberRepository.findByEmail(email);  // 이메일로 DB에서 회원 정보 조회

                if (member != null) {  // 회원 정보가 존재하는 경우
                    if (member.getRole() == Role.ADMIN) {  // 회원의 역할이 관리자일 경우
                        senderName = "운영자";  // 송신자를 "운영자"로 설정
                    } else {
                        senderName = member.getName();  // 일반 회원일 경우 DB에 저장된 회원 이름으로 설정
                    }
                } else {
                    senderName = name != null ? name : "소셜 사용자";  // 회원 정보가 없을 경우 소셜 로그인 이름이나 닉네임을 사용
                }
            }
            else {  // 일반 로그인 사용자인 경우
                String email = principal.getName();  // Principal에서 이메일 정보를 가져옴
                Member member = memberRepository.findByEmail(email);  // 이메일로 DB에서 회원 정보 조회

                if (member != null) {  // 회원 정보가 존재할 경우
                    if (member.getRole() == Role.ADMIN) {  // 회원의 역할이 관리자일 경우
                        senderName = "운영자";  // 송신자를 "운영자"로 설정
                    } else {
                        senderName = member.getName();  // 일반 회원일 경우 회원 이름으로 설정
                    }
                }
            }
        }
        else {  // 로그인되지 않은 사용자인 경우
            senderName = "anonymous";  // 송신자를 "anonymous"로 설정
        }

        return new ChatMessage(message.getContent(), senderName);  // 송신자 이름과 메시지 내용으로 새로운 ChatMessage 객체 반환
    }

    @GetMapping("/current-user")  // "/chat/current-user" 경로에 대한 GET 요청 처리
    @ResponseBody  // 반환 값을 HTTP 응답 본문으로 반환 (JSON 형식으로 반환 가능)
    public String chatRoom(Model model, Principal principal) {
        String email = getEmailFromPrincipalOrSession(principal);  // Principal이나 세션에서 이메일을 가져옴
        Member member = memberRepository.findByEmail(email);  // 이메일로 DB에서 회원 정보 조회
        System.out.println("이메일"+email);  // 이메일 로그 출력

        if (member != null) {  // 회원 정보가 존재할 경우
            if (member.getRole() == Role.ADMIN){  // 회원의 역할이 관리자일 경우
                return "운영자";  // "운영자" 반환
            }
            return member.getName();  // 일반 회원일 경우 회원 이름 반환
        }
        else {
            return "anonymous";  // 회원 정보가 없을 경우 "anonymous" 반환
        }
    }

    @GetMapping("/current-role")  // "/chat/current-role" 경로에 대한 GET 요청 처리
    @ResponseBody  // 반환 값을 HTTP 응답 본문으로 반환
    public String chatRole(Model model, Principal principal) {
        if (principal != null) {  // 사용자가 로그인되어 있을 경우
            String email = getEmailFromPrincipalOrSession(principal);  // Principal이나 세션에서 이메일을 가져옴
            Member member = memberRepository.findByEmail(email);  // 이메일로 DB에서 회원 정보 조회
            return member.getRole().toString();  // 회원의 역할(Role)을 문자열로 반환
        }

        return "뉴비";  // 로그인하지 않은 사용자는 "뉴비" 반환
    }

    private String getEmailFromPrincipalOrSession(Principal principal) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");  // 세션에서 사용자 정보를 가져옴
        if (user != null) {  // 세션에 사용자가 있을 경우
            return user.getEmail();  // 세션에서 이메일 반환
        }
        return principal.getName();  // 세션에 없으면 Principal에서 이메일 반환
    }
}

