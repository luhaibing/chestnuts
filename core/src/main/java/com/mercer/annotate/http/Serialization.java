package com.mercer.annotate.http;

import com.mercer.core.Converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author :Mercer
 * @Created on 2025/01/03.
 * @Description: 序列化
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Serialization {

    @SuppressWarnings("rawtypes")
    Class<? extends Converter.Factory> value();

}