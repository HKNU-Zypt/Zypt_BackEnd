package zypt.zyptapiserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zypt.zyptapiserver.domain.SocialAuth;

public interface SocialAuthRepository extends JpaRepository<SocialAuth, Long> {

}
