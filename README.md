# APPserver 项目描述文档
###### （注：）只讲解框架技术架构不谈代码逻辑
###### 该文档选用 MARKDOWN文档，*.md是其简称,（推荐编写文档可选用 MARKDOWN 语法请百度自行学习）
- 参考网址：https://www.jianshu.com/p/191d1e21f7ed
```
    该项目重构后的框架为SpringBoot架构，boot是spring推出的新型框架技术，
boot核心观念约定优于配置
```
##### pom.xml 配置及规范
```$xslt
    boot项目其本身也属于是一个maven项目，作为一个maven项目那其最关键的点就是pom文件，
boot项目其本身是一个maven项目，内含了诸多常用的jar包，这些jar包的版本升级是和boot的版本
息息相关的，<version>1.5.9.RELEASE</version> 如需升级一些内含jar包版本，只需升级boot版本即可
建议引入jar包版本定义其jar包版本时将其版本定义在上面，如<swagger.version>2.8.0</swagger.version>
在下面如下引用即可 <version>${swagger.version}</version>
（请看pom.xml查看该项目配置及规范）
```
##### boot 打包简述
```$xslt
    打完包的项目会出现在tatget目录下 这里我们把比较大的jar称之为胖jar，小的称为瘦jar
打成的包会有一个胖jar及一个瘦jar，瘦jar的话比较小因为没有将项目所需要的jar加进去，里面只有java编译后的代码！
而胖jar是将我们项目中用到的jar包和代码加到一起所以它的会比较大一些！
```
##### boot项目打war包
```$xslt
    boot打包默认是jar包，如果是jar包时我们无需修改任何配置，如 因项目一些原因我们需要将项目打成war包时我们做如下修改：
1、在pom.xml文件首部增加<packaging>war</packaging>，/
<groupId>com.uloan.ssm</groupId>
<artifactId>Uloan</artifactId>
<version>1.0-SNAPSHOT</version>
<packaging>war</packaging>
<!--<packaging>jar</packaging>-->
2、<!--增加下面的依赖覆盖默认内嵌的Tomcat依赖-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
scope的分类
1.compile：默认值 他表示被依赖项目需要参与当前项目的编译，还有后续的测试，运行周期也参与其中，是一个比较强的依赖。打包的时候通常需要包含进去
2.test：依赖项目仅仅参与测试相关的工作，包括测试代码的编译和执行，不会被打包，例如：junit
3.runtime：表示被依赖项目无需参与项目的编译，不过后期的测试和运行周期需要其参与。与compile相比，跳过了编译而已。例如JDBC驱动，适用运行和测试阶段
4.provided：打包的时候可以不用包进去，别的设施会提供。事实上该依赖理论上可以参与编译，测试，运行等周期。相当于compile，但是打包阶段做了exclude操作
5.system：从参与度来说，和provided相同，不过被依赖项不会从maven仓库下载，而是从本地文件系统拿。需要添加systemPath的属性来定义路径
所以我们这里采用provided，这也是spring官方推荐的方式。
剩下的就是将我们的启动类进行修改，我们之前启动类是boot的启动方式，我们这边要修改成tomcat的方式，当然之前的boot启动方式在我们本地依旧保留使用。
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(UloanApplication.class);
    }
}
该类继承了SpringBootServletInitializer并且重写了configure方法。
jar包和war包启动区别
    jar包:执行SpringBootApplication的run方法,启动IOC容器,然后创建嵌入式Servlet容器
    war包:  先是启动Servlet服务器,服务器启动Springboot应用(springBootServletInitizer),然后启动IOC容器
SpringBootServletInitializer实例执行onStartup方法的时候会通过createRootApplicationContext方法来执行run方法，
接下来的过程就同以jar包形式启动的应用的run过程一样了，在内部会创建IOC容器并返回，只是以war包形式的应用在创建IOC容器过程中，不再创建Servlet容器了。
创建boot项目是可以选择war或jar的方式，上述配置war包方式是官方推荐方式
```
##### 配置文件 由properties 修改为 yml
```$xslt
    在 spring boot 中，有两种配置文件，一种是application.properties,另一种是application.yml,
两种都可以配置spring boot 项目中的一些变量的定义，参数的设置等。下面来说说两者的区别。
application.properties 配置文件在写的时候要写完整，如：
spring.profiles.active=dev
spring.datasource.data-username=root
在yml 文件中配置的话，写法如下：
spring:
  profiles:
    active: prod
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
yml 文件在写的时候层次感强，而且少写了代码。所以现在很多人都使用yml配置文件。

在项目中配置多套环境的配置方法。
    因为现在一个项目有好多环境，开发环境，测试环境，准生产环境，生产环境，每个环境的参数不同，
所以我们就可以把每个环境的参数配置到yml文件中，这样在想用哪个环境的时候只需要在主配置文件中将用的配置文件写上就行如下：
spring:
  profiles:
    active: prod 
这行配置在application.yml 文件中，意思是当前起作用的配置文件是application-prod.yml,其他的配置文件命名为 application-dev.yml,application-test.yml等。
```
##### 配置文件读取方式
```$xslt
@Value("${cashbang.picfolder}")
private String picfolder;
后面你取到变量picfolder 的值就是配置文件中配置的值。
上面方法不是很推荐
下述方法推荐
    我们只需要创建一个实体将我们配置的配置字段写入，这个使用 @Data 注解（lombok jar包注解）可以免生成get set
使用@ConfigurationProperties("cashbang")绑定application.properties中的属性，
在启动类或配置类使用@EnableConfigurationProperties(value = {CashBangUrlProperties.class})使其生效
剩下的就特别简单了，我们只需 @Autowired 注入，如下： 我们就可以用 cashBangUrlProperties.get... 所需的字段使用了。 
@Autowired
private CashBangUrlProperties cashBangUrlProperties;
```
##### lombok jar 包简单描述
```$xslt
    在项目中使用Lombok可以减少很多重复代码的书写。比如说getter/setter/toString等方法的编写。
IDEA中的安装
打开IDEA的Setting –> 选择Plugins选项 –> 选择Browse repositories –> 搜索lombok –> 点击安装 –> 安装完成重启IDEA –> 安装成功
在项目中添加Lombok依赖jar，在pom文件中添加如下配置
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
打包过程中我们不需要lombok jar包如果打包是将其打入没什么大问题但是会增加jar包的大小，所以我们进行如下配置移除：
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <excludes>
            <exclude>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </exclude>
            <exclude>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-configuration-processor</artifactId>
            </exclude>
        </excludes>
    </configuration>
</plugin>
```
##### Lombok有哪些注解 如下：
```$xslt
@Data 注解在类上；提供类所有属性的 getting 和 setting 方法，此外还提供了equals、canEqual、hashCode、toString 方法
@Setter ：注解在属性上；为属性提供 setting 方法
@Setter ：注解在属性上；为属性提供 getting 方法
@Log4j ：注解在类上；为类提供一个 属性名为log 的 log4j 日志对象
@NoArgsConstructor ：注解在类上；为类提供一个无参的构造方法
@AllArgsConstructor ：注解在类上；为类提供一个全参的构造方法
@Cleanup : 可以关闭流
@Builder ： 被注解的类加个构造者模式
@Synchronized ： 加个同步锁
@SneakyThrows : 等同于try/catch 捕获异常
@NonNull : 如果给参数加个这个注解 参数为null会抛出空指针异常
@Value : 注解和@Data类似，区别在于它会把所有成员变量默认定义为private final修饰，并且不会生成set方法。
详细介绍请百度。
```
##### swagger API 生成使用介绍
```$xslt
1：认识Swagger
    Swagger 是一个规范和完整的框架，用于生成、描述、调用和可视化 RESTful 风格的 Web 服务。总体目标是使客户端和文件系统作为服务器以同样的速度来更新。
文件的方法，参数和模型紧密集成到服务器端的代码，允许API来始终保持同步。
     作用：
        1. 接口的文档在线自动生成。
        2. 功能测试。
Swagger是一组开源项目，其中主要要项目如下：
    1.   Swagger-tools:提供各种与Swagger进行集成和交互的工具。例如模式检验、Swagger 1.2文档转换成Swagger 2.0文档等功能。
    2.   Swagger-core: 用于Java/Scala的的Swagger实现。与JAX-RS(Jersey、Resteasy、CXF...)、Servlets和Play框架进行集成。
    3.   Swagger-js: 用于JavaScript的Swagger实现。
    4.   Swagger-node-express: Swagger模块，用于node.js的Express web应用框架。
    5.   Swagger-ui：一个无依赖的HTML、JS和CSS集合，可以为Swagger兼容API动态生成优雅文档。
    6.   Swagger-codegen：一个模板驱动引擎，通过分析用户Swagger资源声明以各种语言生成客户端代码。
    pom.xml引入 如下两个jar包
<!--swagger依赖引入-->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>${swagger.version}</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>${swagger.version}</version>
</dependency>
配置默认首页 如下：
@Override
public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("redirect:/swagger-ui.html");
    registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
}
配置读取，显示信息等，请看Swagger2Configuration.java 文件
@Profile 配置开启环境
```
##### swagger API 注解
```$xslt
常用注解： 
@Api()用于类； 
表示标识这个类是swagger的资源 
用于类；表示标识这个类是swagger的资源 
tags–表示说明 
value–也是说明，可以使用tags替代 
但是tags如果有多个值，会生成多个list

@ApiOperation()用于方法； 
表示一个http请求的操作 
value用于方法描述 
notes用于提示内容 
tags可以重新分组（视情况而用）

@ApiParam()用于方法，参数，字段说明； 
表示对参数的添加元数据（说明或是否必填等） 
name–参数名 
value–参数说明 
required–是否必填

@ApiModel()用于类 
表示对类进行说明，用于参数用实体类接收 
value–表示对象名 
description–描述 

@ApiModelProperty()用于方法，字段 
表示对model属性的说明或者数据操作更改 
value–字段说明 
name–重写属性名字 
dataType–重写属性类型 
required–是否必填 
example–举例说明 
hidden–隐藏

@ApiIgnore()用于类，方法，方法参数 
表示这个方法或者类被忽略 

@ApiImplicitParam() 用于方法 
表示单独的请求参数 

@ApiImplicitParams() 用于方法，包含多个 @ApiImplicitParam
name–参数ming 
value–参数说明 
dataType–数据类型 
paramType–参数类型 
example–举例说明

更多使用及详情请百度百科查询。
```
##### boot 项目跨域配置
```$xslt
     因项目联调需要配置跨域配置，跨域配置极其简单，只需继承WebMvcConfigurerAdapter实现addCorsMappings
/**
* 跨域CORS配置 这里配置允许所有
* @param registry
*/
@Override
public void addCorsMappings(CorsRegistry registry) {
 registry.addMapping("/**").allowedHeaders("*")
         .allowedMethods("*")
         .allowedOrigins("*")
         .maxAge(3628800)
         .allowCredentials(true);
}
```
##### boot 拦截配置
```$xslt
继承 WebMvcConfigurerAdapter 实现方法
WebMvcConfigurerAdapter配置类其实是Spring内部的一种配置方式，采用JavaBean的形式来代替传统的xml配置文件形式进行针对框架个性化定制，Spring 5.0 以后WebMvcConfigurerAdapter会取消掉
以下WebMvcConfigurerAdapter 比较常用的重写接口 
/** 解决跨域问题 **/ public void addCorsMappings(CorsRegistry registry) ;
/** 添加拦截器 **/ void addInterceptors(InterceptorRegistry registry); 
/** 这里配置视图解析器 **/ void configureViewResolvers(ViewResolverRegistry registry);
/** 配置内容裁决的一些选项 **/ void configureContentNegotiation(ContentNegotiationConfigurer configurer); 
/** 视图跳转控制器 **/ void addViewControllers(ViewControllerRegistry registry);
/** 静态资源处理 **/ void addResourceHandlers(ResourceHandlerRegistry registry); 
/** 默认静态资源处理器 **/ void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer);

新的版本解决方案目前有两种：
方案1 直接实现WebMvcConfigurer
@Configuration
public class WebMvcConfg implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
         registry.addViewController("/index").setViewName("index");
    }
}
方案2 直接继承WebMvcConfigurationSupport
@Configuration
public class WebMvcConfg extends WebMvcConfigurationSupport {
   @Override
   public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index");
   }
}
个人推荐使用第一种
/**
* 跨域配置 允许所以请求进来，可根据需求配置
* @param registry
*/
@Override
public void addCorsMappings(CorsRegistry registry) {
registry.addMapping("/**").allowedHeaders("*")
.allowedMethods("*")
.allowedOrigins("*")
.maxAge(3628800)
.allowCredentials(true);
}
/**
* 拦截器配置 拦截所需要拦截的请求，走addInterceptor内new的方法校验
* @param registry
*/
@Override
public void addInterceptors(InterceptorRegistry registry){
if(!devWithoutInterception) {
registry.addInterceptor(new UserInterceptor())
.addPathPatterns("/user/*.do", "/order/*.do")
.excludePathPatterns("/common/*.do", "/wechat/*.do");
registry.addInterceptor(new WebRequestInterceptor())
.addPathPatterns("/html/*.do")
.excludePathPatterns("/common/*.do");
}
}
/**
* FilterRegistrationBean
* 用来配置urlpattern
* 来确定哪些路径触发filter
* order 顺序
*/
@Bean
public FilterRegistrationBean someFilterRegistration() {
FilterRegistrationBean registration = new FilterRegistrationBean();
registration.setFilter(new DecryptFilter());
registration.addUrlPatterns("/user/*","/order/*");
registration.setOrder(1);
return registration;
}
/**
* 配置微信访问拦截
* @return
*/
@Bean
public ServletRegistrationBean indexServletRegistration() {
ServletRegistrationBean registration = new ServletRegistrationBean(new AccessTokenServlet());
registration.addUrlMappings("/AccessTokenServlet");
registration.addInitParameter("appId",wxappid);
registration.addInitParameter("appSecret",wxappSecret);
return registration;
}
/**
* 在Springboot程序启动后，会默认添加OrderedCharacterEncodingFilter和HiddenHttpMethodFilter过滤器。
* 在HiddenHttpMethodFilter过滤器中会调用request.getParameter(),
* 从而造成我们在controller中通过request的InputStream无法读取到RequestBody的数据
* @return
*/
@Bean
public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
return new OrderedHiddenHttpMethodFilter(){
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
throws ServletException, IOException {
filterChain.doFilter(request, response);
}
};
}
想了解更多，请自行学习。
```
##### boot redis及rabbitmq配置
```$xslt
    非boot项目配置redis和rabbitmq时需增加xml配置文件，约定优于配置所有boot项目我们只需在配置文件配置即可，如下：
 #Redis数据库索引（默认为0）
  redis:
    database: 0
    # Redis服务器地址
    host: 127.0.0.1
    # Redis服务器连接密码（默认为空）
    password: root
    # Redis服务器连接端口
    port: 6379
    # 连接超时时间（毫秒）
    timeout: 0
    pool:
      #连接池最大连接数（使用负值表示没有限制）
      max-active: 8
      # 连接池最大阻塞等待时间（使用负值表示没有限制）
      max-wait: -1
      # 连接池中的最大空闲连接
      max-idle: 8
      # 连接池中的最小空闲连接
      min-idle: 0

  #rabbitmq 配置
  application:
    name: springboot-rabbitmq
  rabbitmq:
    #服务地址
    host: 127.0.0.1
    #端口
    port: 5672
    #账户名
    username: root
    #密码
    password: root
    #启用消息确认
    publisher-confirms: true
    #虚拟主机, 虚拟服务器
    virtual-host: /root
我们需在RedisDao文件增加 @Component 
@component （把普通pojo实例化到spring容器中，相当于配置文件中的 
<bean id="" class=""/>）
泛指各种组件，就是说当我们的类不属于各种归类的时候（不属于@Controller、@Services等的时候），我们就可以使用@Component来标注这个类。
案例： 
<context:component-scan base-package=”com.*”> 
上面的这个例子是引入Component组件的例子，其中base-package表示为需要扫描的所有子包。
```

##### 代码生成 采用Beetl模板生成方式
```$xslt
 什么是Beetl
 Beetl目前版本是2.7.23,相对于其他java模板引擎，具有功能齐全，语法直观,性能超高，以及编写的模板容易维护等特点。使得开发和维护模板有很好的体验。是新一代的模板引擎。总得来说，它的特性如下：

 功能完备： 
 作为主流模板引擎，Beetl具有相当多的功能和其他模板引擎不具备的功能。适用于各种应用场景，从对响应速度有很高要求的大网站到功能繁多的CMS管理系统都适合。Beetl本身还具有很多独特功能来完成模板编写和维护，这是其他模板引擎所不具有的。
 非常简单： 
 类似Javascript语法和习俗，只要半小时就能通过半学半猜完全掌握用法。拒绝其他模板引擎那种非人性化的语法和习俗。同时也能支持html 标签，使得开发CMS系统比较容易 
 超高的性能：Beetl 远超过主流java模板引擎性能(引擎性能5-6倍与freemaker，2倍于JSP。参考附录），而且消耗较低的CPU。
 易于整合： 
 Beetl能很容易的与各种web框架整合，如Spring MVC，JFinal，Struts，Nutz，Jodd，Servlet等。 
 支持模板单独开发和测试，即在MVC架构中，即使没有M和C部分，也能开发和测试模板。 
 扩展和个性化：Beetl支持自定义方法，格式化函数，虚拟属性，标签，和HTML标签. 同时Beetl也支持自定义占位符和控制语句起始符号也支持使用者完全可以打造适合自己的工具包。 
 可以扩展为脚本引擎，规则引擎，能定制引擎从而实现高级功能。
 
 生成代码前，修改config.properties配置文件，如目录修改请修改模板目录
 JdbcGenUtils.java 文件，运行main方法生成代码，可配置不同数据库连接，进行代码生成
```

