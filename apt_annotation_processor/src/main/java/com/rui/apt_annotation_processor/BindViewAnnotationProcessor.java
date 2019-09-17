package com.rui.apt_annotation_processor;

import com.google.auto.service.AutoService;
import com.rui.apt_annotation.BindView;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

// 该处理器有几种方式可以定义让编译器处理:
// 第一种方式) 直接利用SPI机制 (配置太麻烦了)
//        1、需要在 processors 库的 main 目录下新建 resources 资源文件夹
//        2、在 resources文件夹下建立 META-INF/services 目录文件夹；
//        3、在 META-INF/services 目录文件夹下创建 javax.annotation.processing.Processor 文件；
//        4、在 javax.annotation.processing.Processor 文件写入注解处理器的全称，包括包路径；）

// 第二种方式) 利用auto-service注解器自动生成META-INF.services文件,需要依赖com.google.auto.service:auto-service:1.0-rc2

@AutoService(Processor.class)
public class BindViewAnnotationProcessor extends AbstractProcessor {

    private Messager mMessager;
    private Elements mElementUtils;
    private Map<String, ClassCreatorProxy> mProxyMap = new HashMap<>();

    // 初始化。可以得到ProcessingEnviroment，ProcessingEnviroment提供很多有用的工具类Elements, Types 和 Filer
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnv.getMessager();
        mElementUtils = processingEnv.getElementUtils();
    }

    // 指定这个注解处理器是注册给哪个注解的，这里说明是注解BindView
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(BindView.class.getCanonicalName());
        return supportTypes;
    }

    // 可以在这里写扫描、评估和处理注解的代码，生成Java文件
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "processing...");
        System.out.print("aaa process...");
        mProxyMap.clear();
        //得到所有使用注解类
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : elements) {
            VariableElement variableElement = (VariableElement) element;
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            String fullClassName = classElement.getQualifiedName().toString();
            mMessager.printMessage(Diagnostic.Kind.NOTE, "processing fullClassName:" + fullClassName);
            ClassCreatorProxy proxy = mProxyMap.get(fullClassName);
            if (proxy == null) {
                proxy = new ClassCreatorProxy(mElementUtils, classElement);
                mProxyMap.put(fullClassName, proxy);
            }
            BindView bindAnnotation = variableElement.getAnnotation(BindView.class);
            int id = bindAnnotation.id();
            proxy.putElement(id, variableElement);
        }
        //通过遍历mProxyMap，创建java文件
        for (String key : mProxyMap.keySet()) {
            ClassCreatorProxy proxyInfo = mProxyMap.get(key);
            try {
//                mMessager.printMessage(Diagnostic.Kind.NOTE, " --> create " + proxyInfo.getProxyClassFullName());
//                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(proxyInfo.getProxyClassFullName(), proxyInfo.getTypeElement());
//                Writer writer = jfo.openWriter();
//                writer.write(proxyInfo.generateJavaCode());
//                writer.flush();
//                writer.close();

                // 使用javaopet生成java文件
                JavaFile javaFile = JavaFile.builder(proxyInfo.getmPackageName(), proxyInfo.generateJavaCode()).build();
                //　生成文件
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                mMessager.printMessage(Diagnostic.Kind.NOTE, " --> create " + proxyInfo.getProxyClassFullName() + "error");
            }
        }

        mMessager.printMessage(Diagnostic.Kind.NOTE, "process finish ...");
        return true;
    }
}
