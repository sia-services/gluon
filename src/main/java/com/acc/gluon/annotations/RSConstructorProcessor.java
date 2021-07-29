package com.acc.gluon.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.acc.gluon.annotations.ResultSetConstructor")
@SupportedSourceVersion(SourceVersion.RELEASE_16)
public class RSConstructorProcessor extends AbstractProcessor {

    private static final Set<TypeKind> PRIMITIVE_TYPES = Set.of(TypeKind.BOOLEAN, TypeKind.INT, TypeKind.LONG, TypeKind.DOUBLE);
    private final Set<Name> DECLARED_TYPES;

    /** public for ServiceLoader */
    public RSConstructorProcessor() {
        var utils = processingEnv.getElementUtils();
        DECLARED_TYPES =  Stream.of(String.class, Integer.class, Long.class, BigDecimal.class).map(c -> utils.getName(c.getName())).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (var e : roundEnv.getElementsAnnotatedWith(ResultSetConstructor.class)) {
            if (e.getKind() == ElementKind.RECORD) {
                processRecord(e, 1);
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,"Element kind: " + e.getKind().name() + " is not a RECORD");
            }
        }
        return true;
    }

    private int processRecord(Element e, int rsIndex) {
        var enclosingElement = e.getEnclosingElement();

        if (e instanceof TypeElement te && enclosingElement instanceof PackageElement pe) {
            String packagename = pe.getQualifiedName().toString();
            return generateImplementation(packagename, te, rsIndex);
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,"Element: " + e.getSimpleName() + "; kind: " + e.getKind().name() + "; enclosing element: " + enclosingElement.getClass());
            return 0;
        }
    }

    private int generateImplementation(String packagename, TypeElement te, int rsIndex) {
        String typeName = te.getSimpleName().toString();
        List<Element> recursiveElements = new ArrayList<>();

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"Record: " + packagename + "." + typeName);

        for (var rc : te.getRecordComponents()) {
            var rcName = rc.getSimpleName();
            TypeMirror rcType = rc.asType();

            var provided = rc.getAnnotation(ResultSetConstructor.Provided.class);
            var join = rc.getAnnotation(ResultSetConstructor.Join.class) != null;

            StringBuilder componentType = processRecordComponent(recursiveElements, rcType, provided, join);

            if (provided != null && provided.value() == Source.ResultSet) {
                rsIndex += 1;
            }

            StringBuilder msg = new StringBuilder("  " + rsIndex + ": " + componentType + " " + rcName);
            if (provided != null) {
                msg.append(" PRV");
                if (join) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,packagename + "." + typeName + "." + rcName + " can be either @Provided or @Join");
                }
            } else if (join) {
                msg.append(" JOIN");
            }

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg.toString());

            if (!join && (provided == null || provided.value() == Source.Code)) {
                rsIndex ++;
            }
        }

        for (var recursiveElement : recursiveElements) {
            rsIndex += processRecord(recursiveElement, rsIndex);
        }

        return rsIndex;
    }

    private StringBuilder processRecordComponent(List<Element> recursiveElements, TypeMirror rcType, ResultSetConstructor.Provided provided, boolean join) {
        var typeKind = rcType.getKind();
        StringBuilder componentType = new StringBuilder();

        if (typeKind == TypeKind.DECLARED) {
            if (rcType instanceof DeclaredType dt) {
                var simpleTypeName = dt.asElement().getSimpleName();
                componentType.append(simpleTypeName);

                // TODO: only Set, List, BigDecimal, String, Integer, Long

                var typeArguments = dt.getTypeArguments();
                if (typeArguments.size() > 0) {
                    if (!join) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,"Recursive type must be annotated with @Join");
                    }
                    if (!(simpleTypeName.contentEquals("Set") || simpleTypeName.contentEquals("List"))) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,"Recursive type must must be enclosed either Set or List");
                    }
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
                } else {
                    if (!DECLARED_TYPES.contains(simpleTypeName)) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,"Not supported type: " + simpleTypeName);
                    }
                }
            }
        } else if (typeKind == TypeKind.ARRAY) {
            if (rcType instanceof ArrayType at) {
                componentType.append("array[ ");

                var arrayType = at.getComponentType();
                componentType.append(arrayType.toString());

                if (arrayType.getKind() != TypeKind.BYTE) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,"Not supported type for array: " + arrayType.toString() + "; must be only byte");
                }

                componentType.append(" ]");
            }
        } else {
            if (!PRIMITIVE_TYPES.contains(typeKind)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,"Not supported primitive type, valid types are: int, long, boolean, double");
            }
            // TODO: only TypeKind.BOOLEAN, TypeKind.INT, TypeKind.LONG TypeKind.DOUBLE
            componentType.append(rcType);
        }
        return componentType;
    }
}
