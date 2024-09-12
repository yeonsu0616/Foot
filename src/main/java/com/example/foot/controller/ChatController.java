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

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage send(ChatMessage message, Principal principal) {
        String senderName = "첫손님";

        if (principal != null) {
            if (principal instanceof OAuth2AuthenticationToken) {
                // 소셜 로그인 사용자 정보 가져오기
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) principal;
                OAuth2User oauthUser = oauthToken.getPrincipal();
                String provider = oauthToken.getAuthorizedClientRegistrationId(); // 소셜 로그인 제공자

                String email = null;
                String name = null;
                System.out.println("소셜 로그인 이메일"+email);
                // 소셜 로그인 제공자에 따라 닉네임 또는 이름을 설정
                if ("kakao".equals(provider)) {
                    // 카카오 로그인: kakao_account에서 이메일 정보 추출
                    Map<String, Object> kakaoAccount = (Map<String, Object>) oauthUser.getAttribute("kakao_account");
                    email = (String) kakaoAccount.get("email");
                    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                    name = (String) profile.get("nickname");
                    System.out.println("카카오 로그인 - 이메일: " + email + ", 닉네임: " + name);

                } else if ("naver".equals(provider)) {
                    // 네이버 로그인: response 내부에서 이메일 정보 추출
                    Map<String, Object> response = (Map<String, Object>) oauthUser.getAttribute("response");
                    email = (String) response.get("email");
                    name = (String) response.get("name");
                    System.out.println("네이버 로그인 - 이메일: " + email + ", 이름: " + name);

                } else if ("google".equals(provider)) {
                    // 구글 로그인: attributes에서 직접 이메일 정보 추출
                    email = oauthUser.getAttribute("email");
                    name = oauthUser.getAttribute("name");
                    System.out.println("구글 로그인 - 이메일: " + email + ", 이름: " + name);
                }

                Member member = memberRepository.findByEmail(email);

                if (member != null) {
                    if (member.getRole() == Role.ADMIN) {
                        senderName = "운영자";
                    } else {
                        senderName = member.getName();  // 회원 정보가 있으면 DB에 저장된 이름 사용
                    }
                } else {
                    senderName = name != null ? name : "소셜 사용자";  // 회원 정보가 없을 경우 소셜 로그인 이름 또는 닉네임 사용
                }
            } else {
                // 일반 로그인 사용자의 정보 처리
                String email = principal.getName();
                Member member = memberRepository.findByEmail(email);

                if (member != null) {
                    if (member.getRole() == Role.ADMIN) {
                        senderName = "운영자";
                    } else {
                        senderName = member.getName();
                    }
                }
            }
        }else{
            senderName ="anonymous";
        }

        return new ChatMessage(message.getContent(), senderName);
    }
    @GetMapping("/current-user")
    @ResponseBody
    public String chatRoom(Model model, Principal principal) {
        String email = getEmailFromPrincipalOrSession(principal);
        Member member =memberRepository.findByEmail(email);
        System.out.println("이메일"+email);
        if (member != null) {
            // 현재 로그인된 사용자의 이름을 model에 추가

            if (member.getRole()==Role.ADMIN){
                return "운영자";
            }
            return member.getName();
        }else{
            return "anonymous";
        }

        // chatRoom.html로 렌더링
    }
    @GetMapping("/current-role")
    @ResponseBody
    public String chatRole(Model model, Principal principal) {
        if (principal != null) {
            // 현재 로그인된 사용자의 이름을 model에 추가
            String email = getEmailFromPrincipalOrSession(principal);
            Member member =memberRepository.findByEmail(email);
            if (member.getRole()==Role.ADMIN){
                return member.getRole().toString();
            }
            return member.getRole().toString();
        }  // 비로그인 사용자는 "anonymous"로 설정

        return "뉴비";  // chatRoom.html로 렌더링
    }
    private String getEmailFromPrincipalOrSession(Principal principal) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            return user.getEmail();
        }
        return principal.getName();
    }
}
