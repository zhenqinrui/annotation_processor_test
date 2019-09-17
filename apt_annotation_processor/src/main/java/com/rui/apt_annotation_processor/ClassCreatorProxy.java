package com.rui.apt_annotation_processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * 作者: zengqinrui on 19/9/16 15:08
 * 邮箱：zengqinrui@szy.cn
 * 功能描述:
 * 备注: 创建java文件的代理类
 */
// 可以手动stringbuild生成文件里面的内容，也可用com.squareup:javapoet:1.10.0来生成文件代码内容

public class ClassCreatorProxy {

    private String mBindingClassName;
    private String mPackageName;
    private TypeElement mTypeElement;
    private Map<Integer, VariableElement> mVariableElementMap = new HashMap<>();

    public ClassCreatorProxy(Elements elements, TypeElement classElement) {
        this.mTypeElement = classElement;
        this.mPackageName = elements.getPackageOf(mTypeElement).getQualifiedName().toString();
        this.mBindingClassName = mTypeElement.getSimpleName().toString() + "_ViewBinding";
    }

    public void putElement(int id, VariableElement element) {
        mVariableElementMap.put(id, element);
    }

//    /**
//     * 创建Java代码
//     * @return
//     */
//    public String generateJavaCode() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("package ").append(mPackageName).append(";\n\n");
//        builder.append("import com.rui.*;\n");
//        builder.append('\n');
//        builder.append("public class ").append(mBindingClassName);
//        builder.append(" {\n");
//
//        generateMethods(builder);
//        builder.append('\n');
//        builder.append("}\n");
//        return builder.toString();
//    }

    // 使用javaopet
    /**
     * 创建Java代码
     * @return
     */
    public TypeSpec generateJavaCode() {
        TypeSpec bindingClass = TypeSpec.classBuilder(mBindingClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateMethods())
                .build();
        return bindingClass;

    }

//    /**
//     * 加入Method
//     * @param builder
//     */
//    private void generateMethods(StringBuilder builder) {
//        builder.append("public void bind(" + mTypeElement.getQualifiedName() + " host ) {\n");
//        for (int id : mVariableElementMap.keySet()) {
//            VariableElement element = mVariableElementMap.get(id);
//            String name = element.getSimpleName().toString();
//            String type = element.asType().toString();
//            builder.append("host." + name).append(" = ");
//            builder.append("(" + type + ")(((android.app.Activity)host).findViewById( " + id + "));\n");
//        }
//        builder.append("  }\n");
//    }

    // 使用javaopet生成代码
    /**
     * 加入Method
     */
    private MethodSpec generateMethods() {
        ClassName host = ClassName.bestGuess(mTypeElement.getQualifiedName().toString());
        // 即这句代码生成 public void bind(Type host)
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(host, "host");
        // 生成 {
//            host.tv = (TextView)(((android.app.Activity)host).findViewById(id);
        // }
        for (int id : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            methodBuilder.addCode("host." + name + " = " + "(" + type + ")(((android.app.Activity)host).findViewById( " + id + "));\n");
        }
        return methodBuilder.build();
    }

    public String getProxyClassFullName() {
        return mPackageName + "." + mBindingClassName;
    }

    public TypeElement getTypeElement() {
        return mTypeElement;
    }

    public String getmPackageName() {
        return mPackageName;
    }
}
