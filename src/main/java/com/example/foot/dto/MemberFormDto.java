package com.example.foot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Setter
public class MemberFormDto {
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email
    private String email;
    @NotEmpty(message = "비밀번호은 필수 입력 값입니다.")
    @Length(min = 8, max = 16, message = "비밀번호는 8자이상, 16자 이하로 입력해주세요.")
    private String password;
    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String address;
    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String tel;

    private String sex; // "남자", "여자"


    private String style; // "공격", "밸런스", "수비"

    private String skills; // "슛", "패스", "드리블", "체력", "스피드", "피지컬", "골키퍼"

    private String picture;
}
