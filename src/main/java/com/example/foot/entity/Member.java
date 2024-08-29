package com.example.foot.entity;

import com.example.foot.constant.Role;
import com.example.foot.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    public Member(String name, String email, String address){
        this.name=name;
        this.email=email;
        this.address=address;
    }
}
