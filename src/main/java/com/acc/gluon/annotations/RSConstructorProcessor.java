package com.acc.gluon.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.acc.gluon.annotations.ResultSetConstructor")
@SupportedSourceVersion(SourceVersion.RELEASE_16)
public class RSConstructorProcessor extends AbstractProcessor {

    /** public for ServiceLoader */
    public RSConstructorProcessor() {}

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean ret = true;
        for (var e : roundEnv.getElementsAnnotatedWith(ResultSetConstructor.class)) {
            if (e.getKind() == ElementKind.RECORD) {
                var processResult = processRecord(e);
                ret = ret && processResult;
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,"Element kind: " + e.getKind().name() + " is not a RECORD");
            }
        }
        return ret;
    }

    private boolean processRecord(Element e) {
        var enclosingElement = e.getEnclosingElement();

        if (e instanceof TypeElement te && enclosingElement instanceof PackageElement pe) {
            return generateImplementation(pe, te);
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,"Element: " + e.getSimpleName() + "; kind: " + e.getKind().name() + "; enclosing element: " + enclosingElement.getClass());
            return false;
        }
    }

    private boolean generateImplementation(PackageElement pe, TypeElement te) {
        boolean ret = true;

        String packagename = pe.getQualifiedName().toString();
        String typeName = te.getSimpleName().toString();

        List<Element> recursiveElements = new ArrayList<>();

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"Record: " + packagename + "." + typeName);

        for (var rc : te.getRecordComponents()) {
            var rcName = rc.getSimpleName();
            TypeMirror rcType = rc.asType();

            var provided = rc.getAnnotation(ResultSetConstructor.Provided.class) != null;
            var join = rc.getAnnotation(ResultSetConstructor.Join.class) != null;

            var typeKind = rcType.getKind();

            StringBuilder componentType = new StringBuilder();

            if (typeKind == TypeKind.DECLARED) {
                // var rcte = processingEnv.getTypeUtils().asElement(rcType);

                if (rcType instanceof DeclaredType dt) {
                    componentType.append(dt.asElement().getSimpleName());
                    var typeArguments = dt.getTypeArguments();
                    if (typeArguments.size() > 0) {
                        componentType.append("< ");
                        for (var ta : dt.getTypeArguments()) {
                            // TODO: recursively analize; temporary create new HashSet<>() for Set && new ArrayList<>() for List
                            componentType.append(ta);
                            var rcte = processingEnv.getTypeUtils().asElement(ta);
                            if (rcte.getKind() == ElementKind.RECORD) {
                                recursiveElements.add(rcte);
                            } else {
                                componentType.append("{").append(rcte.getKind()).append("}");
                            }
                        }
                        componentType.append(" >");
                    }
                }
            } else {
                componentType.append(rcType);
            }

            StringBuilder msg = new StringBuilder(";    " + componentType + " " + rcName + " [" + typeKind.name() + "]");
            if (provided) {
                msg.append(" PRV");
            }else if (join) {
                msg.append(" JOIN");
            }

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg.toString());
        }

        for (var recursiveElement : recursiveElements) {
            var processResult = processRecord(recursiveElement);
            ret = ret && processResult;
        }

        return ret;
    }
}
