package com.yc.springframework.context;

import com.yc.springframework.stereotype.*;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class MyAnnotationConfigApplicationContext implements MyApplicationContext {

    private Map<String,Object> beanMap=new HashMap<>();


    public MyAnnotationConfigApplicationContext(Class<?>... componentClasses)  {
        try {
            register(componentClasses);
        }catch (Exception e){

        }

    }

    private void register(Class<?>[] componentClasses) throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException, ClassNotFoundException {
        //请实现这个里面的代码
        //源码1：只实现IOC,MyPostConstruct   MyPreDestory
        if(componentClasses==null||componentClasses.length<=0){
            throw new RuntimeException("没有指定扫描的配置类.......");
        }
        //MyAppConfig
        for(Class cl:componentClasses){
            //判断是否有@MyConfiguration
            if(!cl.isAnnotationPresent(MyConfiguration.class)){
                //表明扫描的可能是@MyComponentScan(basePackages={"com.yc.bean"})
                //继续下一次循环
                continue;
            }

            //默认是MyAppConfig所在的包下面
            String[] basePackages=getAppConfigBasePackages(cl);
            //判断类上是否有@MyComponentScan(basePackages={"com.yc.bean"})
            //方法上面也类似
            if(cl.isAnnotationPresent(MyComponentScan.class)){
                //根据注解的字节文件，获取注解类
                MyComponentScan mcs= (MyComponentScan) cl.getAnnotation(MyComponentScan.class);
                //注意mcs.basePackages()
                if(mcs.basePackages()!=null&&mcs.basePackages().length>0){
                    basePackages= mcs.basePackages();
                }
            }
            //创建MyAppConfig对象
            Object obj=cl.newInstance();
            //处理@Mybean的情况
            handleAtMyBean(cl,obj);

            /////////////////////////////////////
            //处理basePackages基础包下的所有的托管bean
            //com.yc.bean     com.yc.biz
            for(String basePackage:basePackages){
                scanPackageAndSubPackageClasses(basePackage);
            }
            //根据扫描到的类，托管componentScan目录下，有@Component注解等修饰的类
            handelManagedBean();
            //版本2：实现DI
            //循环beanMap的每个bean，找到它们每个类中的每个由@Autowired @Resource注解的方法以实现di
            hanleDi(beanMap);
            //////////////////////////////////////////////

        }
    }


    ////////////////////////////////////
    /**
     * 循环beanMap的每个bean，找到它们每个类中的每个由@Autowired @Resource注解的方法以实现di
     * @param beanMap
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void hanleDi(Map<String,Object> beanMap) throws InvocationTargetException, IllegalAccessException {
        Collection<Object> objectCollection=beanMap.values();
        //先循环被托管的对象
        for (Object obj:objectCollection){
            Class cls=obj.getClass();
            Method[] ms=cls.getDeclaredMethods();
            //再循环被托管对象的方法
            for(Method m:ms){
                if(m.isAnnotationPresent(MyAutowired.class)&&m.getName().startsWith("set")){
                    invokeAutowiredMethod(m,obj);
                }else if(m.isAnnotationPresent(MyResource.class)&&m.getName().startsWith("set")){
                    invokeResourceMethod(m,obj);
                }
            }
            Field[] fs=cls.getDeclaredFields();
            for(Field field:fs){
                if(field.isAnnotationPresent(MyAutowired.class)){

                }else if(field.isAnnotationPresent(MyResource.class)){

                }
            }
        }
    }

    private void invokeResourceMethod(Method m, Object obj) throws InvocationTargetException, IllegalAccessException {
        //1.取出MyResource中的name属性值，当成beanId
        MyResource mr=m.getAnnotation(MyResource.class);
        String beanId=mr.name();
        //2.@MyResource先根据名字查找，再根据类型查找
        // 如果没有，则取出m方法中参数的类型名，改成首字母小写，当成beanId
        if(beanId==null||beanId.equalsIgnoreCase("")){
            String pname=m.getParameterTypes()[0].getSimpleName();
            beanId= pname.substring(0,1).toLowerCase()+pname.substring(1);
        }
        //3.从beanMap取出
        Object o=beanMap.get(beanId);
        //4.invoke
        m.invoke(obj,o);
    }

    private void invokeAutowiredMethod(Method m, Object obj) throws InvocationTargetException, IllegalAccessException {
        //1.取出被@MyAutowired注解的参数的类型
        Class typeClass=m.getParameterTypes()[0];
        //2.从beanMap(已经扫描且new了的)中循环所有的键集
        Set<String> keys=beanMap.keySet();
        for(String key:keys){
            //根据循环的键名取对象
            Object o=beanMap.get(key);
            //3.判断取出的对象名字是不是参数的类型名
           Class[] interfaces=o.getClass().getInterfaces();
           for (Class c:interfaces){
               System.out.println(c.getName()+"\t"+typeClass);
               if(c==typeClass){
                   m.invoke(obj,o);
                   break;
               }
           }
            //TODO:没有对象的话，根据@MyQualifier名取对象
        }


    }

    /**
     * 处理managedBeanClasses的所有class类，筛选出所有的@Component  @Service  @Repository的类，并实例化，存到beanMap中
     */
    private void handelManagedBean() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        for(Class c:managedBeanClasses){
            if (c.isAnnotationPresent(MyComponent.class)){
                saveManagedBean(c);
            }else if (c.isAnnotationPresent(MyService.class)){
                saveManagedBean(c);
            }else if (c.isAnnotationPresent(MyController.class)){
                saveManagedBean(c);
            }else if (c.isAnnotationPresent(MyRepository.class)){
                saveManagedBean(c);
            }else{
                continue;
            }

        }
    }

    private void saveManagedBean(Class c) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        //根据cls实例创建对象
        Object o=c.newInstance();
        handlePostConstruct(o,c);
        String beanId=c.getSimpleName().substring(0,1).toLowerCase()+c.getSimpleName().substring(1);
        beanMap.put(beanId,o);
    }




    /**
     * 扫描包和子包
     * @param basePackage
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void scanPackageAndSubPackageClasses(String basePackage) throws IOException, ClassNotFoundException {
        String packagePath=basePackage.replaceAll("\\.","/");
        //扫描包路径：com.yc.bean,替换后：com/yc/bean
        System.out.println("扫描包路径："+basePackage+",替换后："+packagePath);
        //使用相对classpath路径，得到绝对路径
        Enumeration<URL> urls=Thread.currentThread().getContextClassLoader().getResources(packagePath);
        while (urls.hasMoreElements()){
            URL url=urls.nextElement();
            //配置的扫描路径为:/C:/Users/Lenovo/Desktop/testSpring/my_springframework/target/classes/com/yc/bean
            //URL是绝对资源路径　url=file:/C:/Users/Lenovo/Desktop/testSpring/my_springframework/target/classes/com/yc/bean
            //url.getFile()获取url形式对应的本地系统的绝对路径/C:/Users/Lenovo/Desktop/testSpring/my_springframework/target/classes/com/yc/bean
            System.out.println("配置的扫描路径为:"+url.getFile());
            //TODO:递归这些目录，查找.class文件
            findClassesInPackages(url.getFile(),basePackage);
        }

    }

    private Set<Class> managedBeanClasses=new HashSet<>();


    /**
     * 查找file下面及子包所有的要托管的class，存到一个set（managedBeanClasses）中
     * @param file
     * @param basePackage
     */
    private void findClassesInPackages(String file, String basePackage) throws ClassNotFoundException {
        //根据绝对路径/C:/Users/Lenovo/Desktop/testSpring/my_springframework/target/classes/com/yc/bean
        //创建文件
        File f=new File(file);
        //列出f文件的所有子文件包括目录
        //File[] ff=f.listFiles();
        //文件过滤器
        File[] classFiles=f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                System.out.println(file.getName());
                //判断如果文件名以.class结尾，或者是一个目录则通过
                return file.getName().endsWith(".class")||file.isDirectory();
            }
        });

        for (File cf:classFiles){
            if(cf.isDirectory()){
                //如果目录，则递归
                //拼接子目录
                String path=basePackage+"."+cf.getName().substring(cf.getName().lastIndexOf("/")+1);
                //根据文件得到绝对路径
                findClassesInPackages(cf.getAbsolutePath(),basePackage);
            }else {
                //加载  cf  作为class文件
                //如果urls为空，则默认从classpath路径下
                URL[] urls=new URL[]{};
                URLClassLoader ucl=new URLClassLoader(urls);
                //com.yc.bean.Hello.class-->com.yc.bean.Hello
                System.out.println("cf.getName():"+cf.getName());
                //一次只加载了一次
                Class c=ucl.loadClass(basePackage+"."+cf.getName().replace(".class",""));
                managedBeanClasses.add(c);
            }
        }
    }
    ///////////////////////////////////

    private void handleAtMyBean(Class cl, Object obj) throws InvocationTargetException, IllegalAccessException {

        //1.获取cls中所有的method
        Method[] ms=cl.getDeclaredMethods();
        //2.循环，判断每个method上是否有@Mybean
        for(Method m:ms){
            if(m.isAnnotationPresent(MyBean.class)){
                //3，有则激活，它有返回值，将返回值存到beanMaP，键是方法名，值是返回值对象
                Object o=m.invoke(obj);
                //4.加入处理@Mybean注解对应的方法所实例化的类中的@MyPostConstruct对应的方法
                //传入o的class实例，是为了判断其方法内有无@PostConstruct
                handlePostConstruct(o,o.getClass());
                beanMap.put(m.getName(),o);
            }
        }
    }

    /**
     * 处理一个bean中的@MyPostConstruct对应的方法
     * @param o
     * @param
     */
    private void handlePostConstruct(Object o, Class<?> cls) throws InvocationTargetException, IllegalAccessException {
        Method[] ms=cls.getDeclaredMethods();
        for(Method m:ms){
            if(m.isAnnotationPresent(MyPostConstruct.class)){
                m.invoke(o);
            }
        }
    }


    /**
     * 获取当前AppConfig类所在的包路径
     * @param
     * @return
     */
    private String[] getAppConfigBasePackages(Class cl){
        String[] paths=new String[1];
        //根据类实例，获取包名
        paths[0]=cl.getPackage().getName();
        //cls.getPackage.getName()获取全类名即全路径名
        System.out.println("配置类cls.getPackage().getName():"+cl.getPackage().getName());
        return paths;
    }




    @Override
    public Object getBean(String id) {
        return beanMap.get(id);
    }
}
