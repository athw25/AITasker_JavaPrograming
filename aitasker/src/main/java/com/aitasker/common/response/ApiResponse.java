package com.aitasker.common.response;// ApiResponse.java

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> extends BaseResponse{
    private T data;
    public ApiResponse(){
    }
    public ApiResponse(boolean success, String message, T data){
        super(success, message);
        this.data = data;
    }
}