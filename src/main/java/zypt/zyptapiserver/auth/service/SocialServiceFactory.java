package zypt.zyptapiserver.auth.service;


import org.springframework.beans.factory.annotation.Value;
import zypt.zyptapiserver.domain.enums.SocialType;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;


public class SocialServiceFactory {



    private static final String SOCIAL_TYPE_HEADER = "SocialType";
    private final Map<SocialType, SocialService> serviceMap;

    public SocialServiceFactory(Map<SocialType, SocialService> serviceMap) {
        this.serviceMap = serviceMap;
    }

    public SocialService getService(HttpServletRequest request) {
        String type = request.getHeader(SOCIAL_TYPE_HEADER);
        SocialType socialType = SocialType.from(type);


        SocialService service = serviceMap.get(socialType);

        // 소셜 타입에 맞는 서비스가 없다면 null을 반환
        if (service == null) {
            throw new IllegalArgumentException("지원하지 않는 소셜 타입 " + type);
        }

        return service;
    }
    public SocialService getService(SocialType type) {
        SocialService service = serviceMap.get(type);

        // 소셜 타입에 맞는 서비스가 없다면 null을 반환
        if (service == null) {
            throw new IllegalArgumentException("지원하지 않는 소셜 타입 " + type);
        }

        return service;
    }


}
