package com.acc.gluon.annotations;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
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
            // processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"Element kind: " + e.getKind().name());
            if (e.getKind() == ElementKind.RECORD) {
                String name = e.getSimpleName().toString();
                TypeElement clazz = (TypeElement) e.getEnclosingElement();

                try {
                    JavaFileObject f = processingEnv.getFiler().
                            createSourceFile(clazz.getQualifiedName() + "Extras");
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                            "Creating " + f.toUri());
                    Writer w = f.openWriter();
                    try {
                        PrintWriter pw = new PrintWriter(w);
                        pw.println("package "
                                + clazz.getEnclosingElement().getSimpleName() + ";");
                        pw.println("public abstract class "
                                + clazz.getSimpleName() + "Extras {");
                        pw.println("    protected " + clazz.getSimpleName()
                                + "Extras() {}");
                        TypeMirror type = e.asType();
                        pw.println("    /** Handle something. */");
                        pw.println("    protected final void handle" + name
                                + "(" + type + " value) {");
                        pw.println("        System.out.println(value);");
                        pw.println("    }");
                        pw.println("}");
                        pw.flush();
                    } finally {
                        w.close();
                    }
                } catch (IOException x) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, x.toString());
                }
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,"Element kind: " + e.getKind().name() + " is not a RECORD");
                continue;
            }
        }
        return true;
    }
}
