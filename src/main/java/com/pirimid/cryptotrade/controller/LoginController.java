package com.pirimid.cryptotrade.controller;

import com.pirimid.cryptotrade.DTO.LoginReqDTO;
import com.pirimid.cryptotrade.DTO.LoginResDTO;
import com.pirimid.cryptotrade.security.config.MyUserDetailService;
import com.pirimid.cryptotrade.security.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManger;

    @Autowired
    MyUserDetailService userDetailService;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReqDTO loginReqDTO){
        LoginResDTO loginResDTO = new LoginResDTO();
        try{
            authenticationManger.authenticate(new UsernamePasswordAuthenticationToken(loginReqDTO.getEmail(),loginReqDTO.getPassword()));
        }catch (BadCredentialsException e){
            loginResDTO.setErrorMessage("Invalid email or password");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(loginResDTO);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong. Try again later");
        }
        final UserDetails userDetails = userDetailService.loadUserByUsername(loginReqDTO.getEmail());
        final String jwtToken = jwtUtil.generateToken(userDetails);
        loginResDTO.setJwt(jwtToken);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(loginResDTO);
    }
}
