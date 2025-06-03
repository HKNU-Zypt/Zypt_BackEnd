package zypt.zyptapiserver.auth.service.oidc;

public record OIDCPublicKeyDto(String kid, String kty, String alg, String use, String n, String e) {

}
