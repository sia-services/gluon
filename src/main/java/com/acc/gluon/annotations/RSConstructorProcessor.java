package com.acc.gluon.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.PackageElement;
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
    public RSConstructorProcessor() {}

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (var e : roundEnv.getElementsAnnotatedWith(ResultSetConstructor.class)) {
            if (e.getKind() == ElementKind.RECORD) {
                var enclosingElement = e.getEnclosingElement();

                if (e instanceof TypeElement te && enclosingElement instanceof PackageElement pe) {
                    generateImplementation(pe, te);
                } else {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,"Element: " + e.getSimpleName() + "; kind: " + e.getKind().name() + "; enclosing element: " + enclosingElement.getClass());
                }
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,"Element kind: " + e.getKind().name() + " is not a RECORD");
            }
        }
        return true;
    }

    private void generateImplementation(PackageElement pe, TypeElement te) {
        String packagename = pe.getQualifiedName().toString();
        String typeName = te.getSimpleName().toString();

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"Record: " + packagename + "." + typeName);

        for (var rc : te.getRecordComponents()) {
            var rcName = rc.getSimpleName();
            TypeMirror rcType = rc.asType();

            for (var am : rc.getAnnotationMirrors()) {
                System.out.println("; ann type: " + am.getAnnotationType().toString());
            }

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,";   component: " + rcType + " " + rcName);
        }
    }
}
