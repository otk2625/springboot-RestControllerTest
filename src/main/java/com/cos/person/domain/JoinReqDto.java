package com.cos.person.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class JoinReqDto {
	
	@NotNull(message = "유저네임업성")
	@NotBlank(message = "유저네임 블랭크")
	@Size(max = 20 , message="넘어갔다~")
	private String username;
	
	@NotNull(message = "패스워드업성")
	private String password;
	
	
	private String phone;
}