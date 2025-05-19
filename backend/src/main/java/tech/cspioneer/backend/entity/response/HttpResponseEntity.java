package tech.cspioneer.backend.entity.response;
//@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)

import java.io.Serializable;

public class HttpResponseEntity implements Serializable {

    private String code; //状态码
    private Object data; //内容
    private String message; //状态消息


    public HttpResponseEntity() {
    }


    public HttpResponseEntity(String code, Object data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}