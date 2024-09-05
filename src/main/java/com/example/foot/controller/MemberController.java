package com.example.foot.controller;

import com.example.foot.dto.MemberFormDto;
import com.example.foot.entity.Member;
import com.example.foot.service.MailService;
import com.example.foot.service.MemberService;
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
//    @GetMapping(value = "/delivery")
//    public String delivery(){
//        return "/delivery/delivery tracking";
//    }

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

    @GetMapping(value = "/memberinfo")
    public String memberinfo(@ModelAttribute MemberFormDto memberFormDto, Model model, Authentication authentication){
//        String name = memberFormDto.getName();
//        System.out.println("Received name: " + name );
//
//        Member member = memberService.findByName(name);
//        model.addAttribute("member",member); // 모델에 멤버 정보를 추가
//        System.out.println(name);
        // 로그인된 사용자의 이메일 가져오기
        String email = authentication.getName(); // 기본적으로 이메일이 username에 저장된 경우

        // 이메일을 통해 회원 정보를 가져오기
        Member member = memberService.findByEmail(email);

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
