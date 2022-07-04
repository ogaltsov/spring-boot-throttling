package com.github.ogaltsov.amzscouttesttask.annotation;

import com.github.ogaltsov.amzscouttesttask.exception.UserRequestOutOfQuotaException;
import com.github.ogaltsov.amzscouttesttask.component.quoting.UserQuotingService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class QuotedBeanPostProcessor implements BeanPostProcessor, Ordered {

    private final Map<String, Class<?>> map = new HashMap<>();

    private final UserQuotingService userQuotingService;

    public QuotedBeanPostProcessor(UserQuotingService userQuotingService) {
        this.userQuotingService = userQuotingService;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beenClass = bean.getClass();
        var isAnnotationPresent = Arrays.stream(beenClass.getMethods())
            .anyMatch(it -> it.isAnnotationPresent(Quoted.class));

        if (isAnnotationPresent) {
            map.put(beanName, beenClass);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {

        Class<?> beanClass = map.get(beanName);
        if (beanClass != null) {

            if (ClassUtils.getAllInterfacesForClass(beanClass).length == 0) {
                return Enhancer.create(
                    beanClass,
                    (InvocationHandler) (o, method, args) -> wrapInvocation(beanClass, bean, method, args));
            } else return Proxy.newProxyInstance(
                beanClass.getClassLoader(),
                new Class[]{beanClass},
                (proxy, method, args) -> wrapInvocation(beanClass, bean, method, args));
        }
        return bean;
    }

    private Object wrapInvocation(Class<?> beanClass, Object bean, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Method classMethod = beanClass.getMethod( method.getName(), method.getParameterTypes() );
        if ( classMethod.isAnnotationPresent(Quoted.class) || beanClass.isAnnotationPresent(Quoted.class) ) {

            var remoteAddr = Optional.ofNullable( RequestContextHolder.getRequestAttributes() )
                .map( it -> ((ServletRequestAttributes) it).getRequest() )
                .map( it -> it.getHeader("REMOTE_ADDR") )
                .orElseThrow();

            if ( userQuotingService.isQuoteEnabled(remoteAddr, beanClass.getName() + method.getName()) ) {
                return method.invoke( bean, args );
            } else {
                throw new UserRequestOutOfQuotaException();
            }
        } else {
            return method.invoke( bean, args );
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
