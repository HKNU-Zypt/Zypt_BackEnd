package fstt.fsttapiserver.auth.service;

import fstt.fsttapiserver.auth.user.CustomUserDetails;
import fstt.fsttapiserver.domain.Member;
import fstt.fsttapiserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository repository;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member member = repository.findById(memberId).orElseThrow(
                () -> new UsernameNotFoundException("not found loginId : " + memberId));

        return new CustomUserDetails(member.getId(), "ROLE_USER");
    }

}
