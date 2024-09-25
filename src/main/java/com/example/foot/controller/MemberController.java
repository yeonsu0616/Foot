package com.example.foot.controller;

import com.example.foot.dto.MemberFormDto;
import com.example.foot.dto.SessionUser;
import com.example.foot.entity.Member;
import com.example.foot.service.MailService;
import com.example.foot.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

import static org.eclipse.jdt.internal.compiler.parser.Parser.name;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final HttpSession httpSession;
    String confirm = "";
    boolean confirmCheck = false;


    @GetMapping(value = "/new")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto",new MemberFormDto());
        return "member/memberForm";
    }
    @PostMapping(value = "/new")
    public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult,
                             Model model) {
        // @Valid 붙은 객체를 검사해서 결과에 에러가 있으면 실행
        if(bindingResult.hasErrors()){
            return "member/memberForm";//다시 회원가입으로 돌려보닙니다.
        }
        if(!confirmCheck){
            model.addAttribute("errorMessage","이메일 인증을 하세요.");
            return "member/memberForm";
        }
        try{
            //Member 객체 생성
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            //데이터베이스에 저장
            memberService.saveMember(member);
        }
        catch (IllegalStateException e){
            model.addAttribute("errorMessage",e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginMember(){
        return "member/memberLoginForm";
    }
    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg","아이디 또는 비밀번호를 확인해주세요");
        return "member/memberLoginForm";
    }

    @PostMapping("/{email}/emailConfirm")
    public @ResponseBody ResponseEntity emailConfirm(@PathVariable("email") String email)
        throws Exception{
        confirm = mailService.sendSimpleMessage(email);
        return new ResponseEntity<String>("인증 메일을 보냈습니다.", HttpStatus.OK);
    }

    @PostMapping("/{code}/codeCheck")
    public @ResponseBody ResponseEntity codeConfirm(@PathVariable("code")String code)
        throws Exception{
        if(confirm.equals(code)){
            confirmCheck = true;
            return new ResponseEntity<String>("인증 성공하였습니다.",HttpStatus.OK);
        }
        return new ResponseEntity<String>("인증 코드를 올바르게 입력해주세요.",HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/invite")
    public String invite(){
        return "slide/invite";
    }
    @GetMapping(value = "/culture")
    public String culture(){
        return "slide/culture";
    }

    @GetMapping(value = "/team")
    public String team(){
        return "slide/team";
    }
    @GetMapping(value = "/team1")
    public String team1(){
        return "slide/team1";
    }
    private String getEmailFromPrincipalOrSession(Principal principal) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user != null) {
            return user.getEmail();
        }
        return principal.getName();
    }

    @GetMapping(value = "/memberinfo")
    public String memberinfo(@ModelAttribute MemberFormDto memberFormDto, Model model, Authentication authentication, Principal principal){
        // 로그인된 사용자의 이메일 가져오기
        String email = getEmailFromPrincipalOrSession(principal);
        String name = memberFormDto.getEmail();
        // 이메일을 통해 회원 정보를 가져오기
        Member member = memberService.findByEmail(email);
        System.out.println("멤버 이름 : " +principal.getName());
        System.out.println("멤버 찐 이름: "+memberFormDto.getName());
        //소셜 로그인
        if(httpSession.getAttribute("user") != null){
            //email = ((SessionUser)httpSession.getAttribute("user")).getEmail();
            System.out.println("a"+member.getName());
            System.out.println("a"+member.getSex());
            System.out.println("a"+member.getSkills());
            System.out.println("a"+member.getStyle());

        }
        //일반 로그인
        else {
            System.out.println("b"+member.getName());
            System.out.println("b"+member.getSex());
            System.out.println("b"+member.getSkills());
            System.out.println("b"+member.getStyle());
        }

        // 모델에 회원 정보를 추가
        model.addAttribute("member", member);
        return "member/memberinfo";
    }

    @GetMapping(value = "/level")
    public String level(){
        return "LevelSystem/level";
    }

    @GetMapping(value = "/intro")
    public String intro(){
        return "more/intro";
    }

    @GetMapping(value = "/intro4")
    public String intro4(){
        return "more/intro4";
    }


}
