package zypt.zyptapiserver.auth.service.oidc;

import java.io.Serializable;

public record OIDCPublicKeyDto(String kid, String kty, String alg, String use, String n, String e) implements Serializable {

}
