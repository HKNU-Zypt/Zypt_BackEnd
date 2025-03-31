package fstt.fsttapiserver.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fstt.fsttapiserver.auth.user.KakaoUserInfo;
import fstt.fsttapiserver.domain.Member;
import fstt.fsttapiserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private MemberRepository repository;


    @Override
    public UserDetails loadUserByUsername(String userPk) throws UsernameNotFoundException {
        Member member = repository.findById(userPk).orElseThrow(
                () -> new UsernameNotFoundException("not found loginId : " + userPk));
        return null;
    }



}
