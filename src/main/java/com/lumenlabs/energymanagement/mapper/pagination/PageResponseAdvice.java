package com.lumenlabs.energymanagement.mapper.pagination;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.lumenlabs.energymanagement.dto.pagination.PageResponseDTO;

@RestControllerAdvice
public class PageResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> paramType = returnType.getParameterType();
        
        // Verificar se é Page diretamente
        if (Page.class.isAssignableFrom(paramType)) {
            return true;
        }
        
        // Verificar se é ResponseEntity<Page<T>>
        if (ResponseEntity.class.isAssignableFrom(paramType)) {
            Type genericType = returnType.getGenericParameterType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 0) {
                    Type firstArg = actualTypeArguments[0];
                    if (firstArg instanceof ParameterizedType) {
                        Type rawType = ((ParameterizedType) firstArg).getRawType();
                        return Page.class.equals(rawType);
                    }
                    return Page.class.equals(firstArg);
                }
            }
        }
        
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, 
            ServerHttpRequest request, ServerHttpResponse response) {
        
        // Se for ResponseEntity, extrair o body
        if (body instanceof ResponseEntity<?> responseEntity) {
            Object responseBody = responseEntity.getBody();
            if (responseBody instanceof Page<?> page) {
                // Criar novo ResponseEntity com PageResponseDTO
                return ResponseEntity.status(responseEntity.getStatusCode())
                        .headers(responseEntity.getHeaders())
                        .body(new PageResponseDTO<>(page));
            }
        }
        
        // Se for Page diretamente
        if (body instanceof Page<?> page) {
            return new PageResponseDTO<>(page);
        }
        
        return body;
    }
}
