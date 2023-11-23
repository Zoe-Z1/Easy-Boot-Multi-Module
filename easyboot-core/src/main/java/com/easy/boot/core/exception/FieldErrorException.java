package com.easy.boot.core.exception;


import com.easy.boot.core.exception.enums.SystemErrorEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @describe 字段效验错误异常
 * @author zoe
 *
 * @date 2023/7/23
 */
public class FieldErrorException extends BaseException {

    @ApiModel("字段效验错误视图")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError implements Serializable {

        private static final long serialVersionUID = 1910555358396027723L;

        @ApiModelProperty(value = "字段名")
        private String field;

        @ApiModelProperty(value = "错误原因")
        private String message;
    }

    public FieldErrorException(List<FieldError> errors) {
        super(SystemErrorEnum.FIELD_ERROR.getCode(), errors.get(0).getMessage());
    }
}
