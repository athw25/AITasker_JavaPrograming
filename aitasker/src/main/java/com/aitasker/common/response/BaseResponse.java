package com.aitasker.common.response;// BaseResponse.java

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse{
    private boolean success;
    private String message;
}