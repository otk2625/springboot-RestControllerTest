package com.cos.person.domain;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateReqDto {
	
	@NotNull(message="비밀번호 체크!")
	private String password;
	private String phone;
}