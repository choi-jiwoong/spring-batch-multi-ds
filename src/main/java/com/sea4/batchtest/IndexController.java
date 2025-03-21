package com.sea4.batchtest;


import com.sea4.batchtest.user.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class IndexController {

	private final LoginService loginService;

	@GetMapping("/")
	public String index() {
		return "index";
	}
}