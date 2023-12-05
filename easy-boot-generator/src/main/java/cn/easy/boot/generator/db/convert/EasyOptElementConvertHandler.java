package cn.easy.boot.generator.db.convert;

import cn.easy.boot.generator.OptElementEnum;

/**
 * @author zoe
 * @date 2023/8/19
 * @description 字段类型转换实现
 */
public class EasyOptElementConvertHandler implements OptElementConvertHandler {


    @Override
    public OptElementEnum convert(String columnType) {
        return DbColumnTypeEnum.toOptElement(columnType);
    }
}
