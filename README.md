pillbox
=======

re-invent IoC container

DI(Dependency Injection)/IoC(Inverse of Control) Container

DI/IoC容器是轻量开发的缘起，比起J2EE笨拙的JNDI + Service Locater的方式，利用DI/IoC的应用，在可测试性、代码组织上都有了长足进步。被认识是J2EE轻量化的先声。现在IoC容器已经被认为是Java企业开发的基础和核心。经过JSR 330标准化，已经成为java扩展的一部分（javax.inject）。流行容器包括Spring, Guice等。而ThoughtWorks在IoC容器以及Java轻量化的发展、普及上做过很多工作。比如PicoContainer，NanoContainer, Naning等项目均出自ThoughtWorker之手（传奇TWer Paul Hammet，Jon Tierson，5年前后者离开ThoughtWorks，领导了google wave项目）。今天我们就来挑战这个功能。

功能要求

1. The implementation must support Constructor Inject
2. The implementation must support Setter Inject
3. The implementation must support container scope
4. The implementation must provide a configuration mechanism

实现要求

1. Functioning properly, and robust.
2. No other library apart from guava, xunit, testing tools.

参考资料
http://martinfowler.com/articles/injection.html