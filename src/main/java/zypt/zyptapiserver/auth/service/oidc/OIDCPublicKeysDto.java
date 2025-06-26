package zypt.zyptapiserver.auth.service.oidc;

import java.io.Serializable;

public record OIDCPublicKeysDto(OIDCPublicKeyDto[] keys) implements Serializable {
}
