package com.dbumama.market.aop;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class WeappAopInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        WeappAopInvocation invocation = new WeappAopInvocation(inv);
        invocation.invoke();
    }

}
