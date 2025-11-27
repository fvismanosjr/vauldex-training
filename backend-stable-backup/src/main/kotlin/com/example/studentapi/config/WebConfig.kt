package com.example.studentapi.config

import com.example.studentapi.interceptor.QueryParamValidationInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(@param:Autowired private val queryParamValidationInterceptor: QueryParamValidationInterceptor) : WebMvcConfigurer
{
    override fun addInterceptors(registry: InterceptorRegistry)
    {
        registry
            .addInterceptor(queryParamValidationInterceptor)
            .addPathPatterns("/students/**")  // define your API base path here
    }
}
