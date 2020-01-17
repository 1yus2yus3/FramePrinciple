package com.cola.springmvc.servlet;

import com.cola.springmvc.annotation.RequestMapping;
import com.cola.springmvc.annotation.Resource;
import com.cola.springmvc.annotation.Controller;
import com.cola.springmvc.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * Copyright (C), 杭州未智科技有限公司
 *
 * @author: Cola
 * @date: 2019/03/17 14:30
 * @description:
 */
public class DispatcherServlet extends HttpServlet {

    //定义一个数组存放扫描到的class类
    private List<String> classList = new ArrayList<>();
    //IOC容器
    private Map<String,Object> beans = new HashMap<>();
    //定义一个map存放Url
    private Map<String,ObjectMethod> urlHandler = new HashMap<>();

    //存储实例化后的对象和方法
    class ObjectMethod{
        Object instance;
        Method method;

        public ObjectMethod(Object instance, Method method) {
            this.instance = instance;
            this.method = method;
        }

        public Object getInstance() {
            return instance;
        }

        public void setInstance(Object instance) {
            this.instance = instance;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }
    }


    // <load-on-startup>0</load-on-startup>
    //web容器启动时，扫描注解创建IOC容器

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        //扫描包目录得到 com.cola.springmvc
        doScan("com.cola.springmvc");
        //通过反射利用类名得到实例化后的对象 //全路径：com.cola.springmvc.controller.TestController.class
        doInstance();
        //获取实例化好的bean
        doAutowire();
        //URLMapping与controller中的方法构建关系
        urlMapping();
    }

    private void urlMapping() {
        //遍历IOC容器中的bean 判断哪些成员变量有Autowire或者resource注解 等等
        for (Map.Entry<String,Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            //查看controller修饰的类是否有autowire
            if(clazz.isAnnotationPresent(Controller.class)) {
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                String classUrlPath = requestMapping.value();
                //得到controller所有的method
                Method []methods = clazz.getMethods();
                for (Method method:methods) {
                    //判断方法上是否有RequestMapping注解
                    if(method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
                        String methodPath = methodMapping.value();
                        //将 url 作为key  把具体要执行的method作为value
                        urlHandler.put(classUrlPath + methodPath,new ObjectMethod(instance,method));
                    }
                }
            }
        }
    }

    private void doAutowire() {
        //遍历IOC容器中的bean 判断哪些成员变量有Autowire或者resource注解 等等
        for (Map.Entry<String,Object> entry : beans.entrySet()) {
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            //查看controller修饰的类是否有autowire
            if(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class)) {
                Field []fields = clazz.getDeclaredFields();
                //判断是否有该注解
                for (Field field : fields) {
                    if(field.isAnnotationPresent(Resource.class)) {
                        Resource resource = field.getAnnotation(Resource.class);
                        //Resource 标记的value 就是key值
                        String key = resource.value();
                        Object value = beans.get(key);
                        //将resource标注的属性用set方法替换成ioc中的实例化对象
                        /*** 私有属性必须先打开权限才能写入 ****/
                        field.setAccessible(true);

                        try {
                            //第一个参数是该属性所使用的实例化对象 ，第二个参数为该属性的取值其实就一个ioc中的bean对象
                            field.set(instance,value);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
    }

    private void doInstance() {
        for (String className:classList) {
            //com.cola.springmvc.controller.TestController.class => com.cola.springmvc.controller.TestController
            String cn = className.replace(".class","");
            try {
                Class<?> clazz = Class.forName(cn);
                //匹配当前反射出来的对象注解 是否是 controller注解
                if(clazz.isAnnotationPresent(Controller.class)) {
                    //实例化一个对象
                    Object instance = clazz.newInstance();
                    //获取到注解的value值如果value值没有可以类似取类名首字母小写（不在实现）
                    Controller controller = clazz.getAnnotation(Controller.class);
                    String key = controller.value();
                    beans.put(key,instance);

                }else if(clazz.isAnnotationPresent(Service.class)){
                    //实例化一个对象
                    Object instance = clazz.newInstance();
                    //获取到注解的value值如果value值没有可以类似取类名首字母小写（不在实现）
                    Service service = clazz.getAnnotation(Service.class);
                    String key = service.value();
                    beans.put(key,instance);

                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void doScan(String basePackage) {
        URL url = this.getClass().getClassLoader().getResource("/"+basePackage.replaceAll("\\.","/"));
        String fileStr = url.getFile();
        File file = new File(fileStr);
        //查看指定目录下的文件名称子目录名称
        String []filesStr = file.list();

        for (String path : filesStr) {
            String fullPath = fileStr + path;
            File fileCuurent = new File(fullPath);
            //如果当前对应的子目录名称 以子目录继续调用当前方法深入扫描
            if(fileCuurent.isDirectory()) {
                doScan(basePackage +"."+ path);
            }
            //如果这个path为文件则扫描该文件
            else{
                //全路径：com.cola.springmvc.controller.TestController.class
                classList.add(basePackage + "." + fileCuurent.getName());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //处理外部请求uri
        String uri = req.getRequestURI();
        //工程名
        String context = req.getContextPath();
        //截取到具体指向 url
        String urlPath = uri.replace(context,"");
        ObjectMethod objectMethod = urlHandler.get(urlPath);
        if(objectMethod == null) {
            System.out.println("404");
            return;
        }
        try{
            Method method = objectMethod.method;
            //根据req 请求携带的参数和RequestParam注解对应
            Object[] args = handParam(req,resp,method);
            method.invoke(objectMethod.instance,null);
        }catch (Exception e){

        }

    }
    //每一个controller方法上（RequestMapping）参数有注解RequestParam
    private Object[] handParam(HttpServletRequest req, HttpServletResponse resp, Method method) {
        //获取方法上入参的类型列表
        Class<?>[] paramClazzs = method.getParameterTypes();
        Object[] args = new Object[paramClazzs.length];

        int args_i=0;
        int index = 0;

        for (Class<?> paramClazz :paramClazzs) {
            //判定请求类型是不是ServletRequest
            if(ServletRequest.class.isAssignableFrom(paramClazz)) {
                args[args_i++] = req;
            }
            //判定请求类型是不是ServletResponse
            if(ServletResponse.class.isAssignableFrom(paramClazz)) {
                args[args_i++] = resp;
            }
            //判断请求参数上是否有请求注解
            Annotation[] paramAns = method.getParameterAnnotations()[0];


        }

        return args;
    }

}
