package com.acc.gluon.annotations;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("com.acc.gluon.annotations.ResultSetConstructor")
@SupportedSourceVersion(SourceVersion.RELEASE_16)
public class RSConstructorProcessor extends AbstractProcessor {

    /** public for ServiceLoader */
    public RSConstructorProcessor() {
        System.out.println("RSConstructorProcessor new");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("RSConstructorProcessor process");
        for (var e : roundEnv.getElementsAnnotatedWith(ResultSetConstructor.class)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"Element kind: " + e.getKind().name());
        }
        return true;
    }
}
