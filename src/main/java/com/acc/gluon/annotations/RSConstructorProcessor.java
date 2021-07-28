package com.acc.gluon.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.acc.gluon.annotations.ResultSetConstructor")
@SupportedSourceVersion(SourceVersion.RELEASE_16)
public class RSConstructorProcessor extends AbstractProcessor {

    /** public for ServiceLoader */
    public RSConstructorProcessor() {
        // System.out.println("RSConstructorProcessor new");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("RSConstructorProcessor process");
        for (var e : roundEnv.getElementsAnnotatedWith(ResultSetConstructor.class)) {
            if (e.getKind() == ElementKind.RECORD) {
                String name = e.getSimpleName().toString();
                TypeElement clazz = (TypeElement) e.getEnclosingElement();

                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"Element kind: " + e.getKind().name());
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,"Element kind: " + e.getKind().name() + " is not a RECORD");
                continue;
            }
        }
        return true;
    }
}
