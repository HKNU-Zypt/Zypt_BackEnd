package zypt.zyptapiserver.auth.service.ocid;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OCIDPublicKeyDto(String kid, String kty, String alg, String use, String n, String e) {

}
