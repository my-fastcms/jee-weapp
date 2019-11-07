package com.dbumama.market;

import com.dbumama.market.aop.WeappAopInterceptor;
import com.dbumama.market.fixedinterceptor.WeappFixedInterceptors;
import com.dbumama.market.handler.WeappActionHandler;
import com.jfinal.aop.Aop;
import com.jfinal.aop.AopManager;
import com.jfinal.config.*;
import com.jfinal.core.Controller;
import com.jfinal.json.FastJson;
import com.jfinal.json.JsonManager;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.template.Engine;
import com.jfinal.template.ext.directive.NowDirective;
import io.jboot.Jboot;
import io.jboot.aop.JbootAopFactory;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.components.limiter.LimiterManager;
import io.jboot.components.rpc.JbootrpcManager;
import io.jboot.core.listener.JbootAppListenerManager;
import io.jboot.core.log.Slf4jLogFactory;
import io.jboot.db.ArpManager;
import io.jboot.support.shiro.JbootShiroManager;
import io.jboot.support.swagger.JbootSwaggerConfig;
import io.jboot.support.swagger.JbootSwaggerController;
import io.jboot.support.swagger.JbootSwaggerManager;
import io.jboot.utils.*;
import io.jboot.web.controller.JbootControllerManager;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.annotation.JFinalSharedMethod;
import io.jboot.web.directive.annotation.JFinalSharedObject;
import io.jboot.web.directive.annotation.JFinalSharedStaticMethod;
import io.jboot.web.handler.JbootFilterHandler;
import io.jboot.web.handler.JbootHandler;
import io.jboot.web.render.JbootRenderFactory;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class WeappCoreConfig extends JFinalConfig {

    static final Log LOG = Log.getLog(WeappCoreConfig.class);
    private List<Routes.Route> routeList = new ArrayList<>();

    public WeappCoreConfig() {
        AopManager.me().setInjectDependency(true);
        AopManager.me().setAopFactory(JbootAopFactory.me());
        Aop.inject(this);
        JbootAppListenerManager.me().onInit();
    }


    @Override
    public void configConstant(Constants constants) {

        constants.setRenderFactory(JbootRenderFactory.me());
        constants.setDevMode(Jboot.isDevMode());

        constants.setLogFactory(Slf4jLogFactory.me());
        constants.setMaxPostSize(1024 * 1024 * 2000);
        constants.setReportAfterInvocation(false);

        constants.setControllerFactory(JbootControllerManager.me());
        constants.setJsonFactory(() -> new FastJson());
        constants.setInjectDependency(true);

        JbootAppListenerManager.me().onConstantConfig(constants);

    }


    @Override
    public void configRoute(Routes routes) {

        routes.setMappingSuperClass(true);

        List<Class<Controller>> controllerClassList = ClassScanner.scanSubClass(Controller.class);
        if (ArrayUtil.isNotEmpty(controllerClassList)) {
            for (Class<Controller> clazz : controllerClassList) {
                RequestMapping mapping = clazz.getAnnotation(RequestMapping.class);
                if (mapping == null) continue;

                String value = AnnotationUtil.get(mapping.value());
                if (value == null) continue;

                String viewPath = AnnotationUtil.get(mapping.viewPath());

                if (StrUtil.isNotBlank(viewPath)) {
                    routes.add(value, clazz, viewPath);
                } else {
                    routes.add(value, clazz);
                }
            }
        }

        JbootSwaggerConfig swaggerConfig = Jboot.config(JbootSwaggerConfig.class);
        if (swaggerConfig.isConfigOk()) {
            routes.add(swaggerConfig.getPath(), JbootSwaggerController.class, swaggerConfig.getPath());
        }

        JbootAppListenerManager.me().onRouteConfig(routes);

        for (Routes.Route route : routes.getRouteItemList()) {
            JbootControllerManager.me().setMapping(route.getControllerKey(), route.getControllerClass());
        }

        routeList.addAll(routes.getRouteItemList());
    }

    @Override
    public void configEngine(Engine engine) {

        /**
         * now 并没有被添加到默认的指令当中
         * 查看：EngineConfig
         */
        engine.addDirective("now", NowDirective.class);

        List<Class> directiveClasses = ClassScanner.scanClass();
        for (Class clazz : directiveClasses) {
            JFinalDirective directive = (JFinalDirective) clazz.getAnnotation(JFinalDirective.class);
            if (directive != null) {
                engine.addDirective(AnnotationUtil.get(directive.value()), clazz);
            }

            JFinalSharedMethod sharedMethod = (JFinalSharedMethod) clazz.getAnnotation(JFinalSharedMethod.class);
            if (sharedMethod != null) {
                engine.addSharedMethod(ClassUtil.newInstance(clazz));
            }

            JFinalSharedStaticMethod sharedStaticMethod = (JFinalSharedStaticMethod) clazz.getAnnotation(JFinalSharedStaticMethod.class);
            if (sharedStaticMethod != null) {
                engine.addSharedStaticMethod(clazz);
            }

            JFinalSharedObject sharedObject = (JFinalSharedObject) clazz.getAnnotation(JFinalSharedObject.class);
            if (sharedObject != null) {
                engine.addSharedObject(AnnotationUtil.get(sharedObject.value()), ClassUtil.newInstance(clazz));
            }
        }

        JbootAppListenerManager.me().onEngineConfig(engine);
    }


    @Override
    public void configPlugin(Plugins plugins) {

        List<ActiveRecordPlugin> arps = ArpManager.me().getActiveRecordPlugins();
        for (ActiveRecordPlugin arp : arps) {
            plugins.add(arp);
        }

        JbootAppListenerManager.me().onPluginConfig(new JfinalPlugins(plugins));

    }


    @Override
    public void configInterceptor(Interceptors interceptors) {

        interceptors.addGlobalServiceInterceptor(new WeappAopInterceptor());

        JbootAppListenerManager.me().onInterceptorConfig(interceptors);
        JbootAppListenerManager.me().onFixedInterceptorConfig(WeappFixedInterceptors.me());
    }

    @Override
    public void configHandler(Handlers handlers) {

        //先添加用户的handler，再添加jboot自己的handler
        //用户的handler优先于jboot的handler执行
        JbootAppListenerManager.me().onHandlerConfig(new JfinalHandlers(handlers));

        handlers.add(new JbootFilterHandler());
        handlers.add(new JbootHandler());

        //若用户自己没配置 ActionHandler，默认使用 JbootActionHandler
        if (handlers.getActionHandler() == null) {
            handlers.setActionHandler(new WeappActionHandler());
        }

    }

    @Override
    public void onStart() {

        JbootAppListenerManager.me().onStartBefore();

        /**
         * 配置微信accessToken的缓存
         */
        JsonManager.me().setDefaultDatePattern("yyyy-MM-dd HH:mm:ss");

        /**
         * 初始化
         */
        JbootrpcManager.me().init();
        JbootShiroManager.me().init(routeList);
//		JbootScheduleManager.me().init();
        JbootSwaggerManager.me().init();
        LimiterManager.me().init();
//        JbootSeataManager.me().init();

        JbootAppListenerManager.me().onStart();
    }

    @Override
    public void onStop() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        if (drivers != null) {
            while (drivers.hasMoreElements()) {
                try {
                    Driver driver = drivers.nextElement();
                    DriverManager.deregisterDriver(driver);
                } catch (Exception e) {
                    LOG.error(e.toString(), e);
                }
            }
        }
        JbootAppListenerManager.me().onStop();
        //JbootScheduleManager.me().stop();
//        JbootSeataManager.me().stop();
    }

}
