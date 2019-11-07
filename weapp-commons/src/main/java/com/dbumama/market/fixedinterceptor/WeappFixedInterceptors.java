package com.dbumama.market.fixedinterceptor;

import com.jfinal.aop.Aop;
import io.jboot.components.limiter.LimiterInterceptor;
import io.jboot.support.jwt.JwtInterceptor;
import io.jboot.support.metric.JbootMetricInterceptor;
import io.jboot.support.shiro.JbootShiroInterceptor;
import io.jboot.web.cors.CORSInterceptor;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.FixedInterceptorWapper;
import io.jboot.web.fixedinterceptor.FixedInterceptors;
import io.jboot.web.validate.ParaValidateInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class WeappFixedInterceptors extends FixedInterceptors {

    private static final WeappFixedInterceptors me = new WeappFixedInterceptors();

    public static WeappFixedInterceptors me() {
        return me;
    }


    /**
     * 默认的 Jboot 系统拦截器
     */
    private FixedInterceptorWapper[] defaultInters = new FixedInterceptorWapper[]{
            new FixedInterceptorWapper(new LimiterInterceptor(), 10),
            new FixedInterceptorWapper(new CORSInterceptor(), 20),
            new FixedInterceptorWapper(new ParaValidateInterceptor(), 30),
            new FixedInterceptorWapper(new JwtInterceptor(), 40),
            new FixedInterceptorWapper(new JbootShiroInterceptor(), 50),
            new FixedInterceptorWapper(new JbootMetricInterceptor(), 60),
//            new FixedInterceptorWapper(new SeataGlobalTransactionalInterceptor(), 80),
    };

    private List<FixedInterceptorWapper> userInters = new ArrayList<>();

    private FixedInterceptor[] allInters = null;

    private List<FixedInterceptorWapper> inters;

    FixedInterceptor[] all() {
        if (allInters == null) {
            synchronized (this) {
                if (allInters == null) {
                    initInters();
                }
            }
        }
        return allInters;
    }


    private void initInters() {

        FixedInterceptor[] interceptors = new FixedInterceptor[defaultInters.length + userInters.size()];
        inters = new ArrayList<>();
        inters.addAll(Arrays.asList(defaultInters));
        inters.addAll(userInters);
        inters.sort(Comparator.comparingInt(FixedInterceptorWapper::getOrderNo));

        int i = 0;
        for (FixedInterceptorWapper interceptor : inters) {
            interceptors[i++] = interceptor.getFixedInterceptor();
        }

        allInters = interceptors;
    }


    public void add(FixedInterceptor interceptor) {
        Aop.inject(interceptor);
        userInters.add(new FixedInterceptorWapper(interceptor));
    }

    public void add(FixedInterceptor interceptor, int orderNo) {
        if (orderNo < 0) {
            orderNo = 0;
        }
        Aop.inject(interceptor);
        userInters.add(new FixedInterceptorWapper(interceptor, orderNo));
    }

    public List<FixedInterceptorWapper> list() {
        return inters;
    }

}
