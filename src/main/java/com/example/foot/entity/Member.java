package com.example.foot.entity;

import ch.qos.logback.classic.model.LoggerModel;
import com.example.foot.constant.Role;
import com.example.foot.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity{
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    private String tel;

    // 성별 필드 추가
    private String sex;

    // 스타일 필드 추가
    private String style;

    // 자신있는 능력 필드 추가

    private String skills;

    private String picture;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Member() {

    }


    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        member.setTel(memberFormDto.getTel());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setRole(Role.ADMIN);
        member.setPicture(memberFormDto.getPicture());

        //추가된 필드 설정
        member.setSex(memberFormDto.getSex());
        member.setStyle(memberFormDto.getStyle());
        member.setSkills(memberFormDto.getSkills());
        return member;
    }

    public static Member createSocialMember(String name, String email, String picture){
        Member member = new Member();
        member.setName(name);
        member.setEmail(email);
        member.setRole(Role.USER);
        member.setPicture(picture);

        return member;
    }

    public Member updateMember(String name, String picture){
        this.name = name;
        this.picture = picture;
        return this;
    }
    @Builder
    public Member(String name, String email, String address,String sex,String style,String skills){
        this.name=name;
        this.email=email;
        this.address=address;
        this.sex = sex;
        this.style = style;
        this.skills = skills;
    }



}
