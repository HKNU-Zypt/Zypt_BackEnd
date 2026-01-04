package zypt.zyptapiserver.dto;


import lombok.Getter;

@Getter
public class ApiErrorResponse {
    private int code;
    private String message;
    private String detail;

    public ApiErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void addDetail(String detail) {
        this.detail = detail;
    }

    public ApiErrorResponse(int code, String message, String detail) {

        this.code = code;
        this.message = message;
        this.detail = detail;
    }
}

