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

import static com.acc.gluon.annotations.GeneratorHelpers.printClass;
import static com.acc.gluon.annotations.GeneratorHelpers.printMethod;
import static com.acc.gluon.utilities.StringUtils.capitalize;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.acc.gluon.annotations.ResultSetConstructor")
@SupportedSourceVersion(SourceVersion.RELEASE_16)
public class RSConstructorProcessor extends AbstractProcessor {
    private static final String PACKAGE_SUFFIX = ".impl";
    private static final String IMPL_CLASS_SUFFIX = "Constructor";

    private static final String BLOB_EXTRACTOR = "com.acc.gluon.sql.Extractors.getBlob(rs, ";
    private static final String BOOLEAN_EXTRACTOR = "com.acc.gluon.sql.Extractors.getBoolean(rs, ";
    private static final String INTEGER_EXTRACTOR = "com.acc.gluon.sql.Extractors.getNullableInt(rs, ";
    private static final String LONG_EXTRACTOR = "com.acc.gluon.sql.Extractors.getNullableLong(rs, ";

    private static final Set<TypeKind> PRIMITIVE_TYPES = Set.of(TypeKind.BOOLEAN, TypeKind.INT, TypeKind.LONG, TypeKind.DOUBLE);
    private Set<String> DECLARED_TYPES = null;

    /**
     * public for ServiceLoader
     */
    public RSConstructorProcessor() {
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (DECLARED_TYPES == null) {
            DECLARED_TYPES = Stream.of(String.class, Integer.class, Long.class, BigDecimal.class).map(Class::getSimpleName).collect(Collectors.toUnmodifiableSet());
        }

        for (var e : roundEnv.getElementsAnnotatedWith(ResultSetConstructor.class)) {
            if (e.getKind() == ElementKind.RECORD) {
                processRecord(e, 1);
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Element kind: " + e.getKind().name() + " is not a RECORD");
            }
        }
        return true;
    }

    private int processRecord(Element e, int rsIndex) {
        var enclosingElement = e.getEnclosingElement();

        if (e instanceof TypeElement te && enclosingElement instanceof PackageElement pe) {
            String packagename = pe.getQualifiedName().toString();
            return processRecord(packagename, te, rsIndex);
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Element: " + e.getSimpleName() + "; kind: " + e.getKind().name() + "; enclosing element: " + enclosingElement.getClass());
            return 0;
        }
    }

    private int processRecord(String packagename, TypeElement te, int rsIndex) {
        List<Element> recursiveElements = new ArrayList<>();

        String typename = te.getSimpleName().toString();

        try {
            JavaFileObject f = processingEnv
                    .getFiler()
                    .createSourceFile(packagename+ PACKAGE_SUFFIX + "." + typename + IMPL_CLASS_SUFFIX);

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Creating to " + f.toUri());

            try (Writer w = f.openWriter()) {
                PrintWriter pw = new PrintWriter(w);
                rsIndex = generateImplementation(pw, packagename, typename, te, rsIndex, recursiveElements);
                pw.flush();
            }
        } catch (IOException x) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    x.toString());
        }

        for (var recursiveElement : recursiveElements) {
            rsIndex += processRecord(recursiveElement, rsIndex);
        }

        return rsIndex;
    }

    private int generateImplementation(PrintWriter pw, String packagename, String typename, TypeElement te, int rsIndex, List<Element> recursiveElements) {
        String fqcn = packagename + "." + typename;

        int[] localIndex = new int[] { rsIndex };

        printClass(pw, packagename + PACKAGE_SUFFIX, typename + IMPL_CLASS_SUFFIX, () -> {

            boolean firstRecord = true;
            // print function header
            ArrayList<String> arguments = new ArrayList<>();
            StringBuilder functionBody = new StringBuilder("    return new " + fqcn + "(");

            arguments.add("java.sql.ResultSet rs");

            for (var rc : te.getRecordComponents()) {
                var rcName = rc.getSimpleName();
                TypeMirror rcType = rc.asType();

                var provided = rc.getAnnotation(ResultSetConstructor.Provided.class);
                var join = rc.getAnnotation(ResultSetConstructor.Join.class) != null;

                if (provided != null) {
                    arguments.add(rcType.toString() + " " + rcName);
                }

                if (firstRecord) {
                    firstRecord = false;
                } else {
                    functionBody.append(",");
                }
                functionBody.append("\n      ");
                if (provided != null) {
                    functionBody.append(rcName);
                } else {
                    functionBody.append( processRecordComponent(localIndex[0], rcType, join, recursiveElements) );
                }
                functionBody.append("\n");

                if (provided != null && provided.value() == Source.ResultSet) {
                    localIndex[0]++;
                }

                if (!join && provided == null) {
                    localIndex[0]++;
                }
            }

            functionBody.append(");");
            printMethod(pw, "construct", fqcn, true, true, arguments, functionBody);

        });

        return localIndex[0];
    }

    private StringBuilder processRecordComponent(int rsIndex, TypeMirror rcType, boolean join, List<Element> recursiveElements) {
        var typeKind = rcType.getKind();

        StringBuilder expression = new StringBuilder();

        if (typeKind == TypeKind.DECLARED) {
            if (rcType instanceof DeclaredType dt) {
                var simpleTypeName = dt.asElement().getSimpleName();

                // only Set, List, BigDecimal, String, Integer, Long

                var typeArguments = dt.getTypeArguments();
                if (typeArguments.size() > 0) {
                    if (!join) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Recursive type must be annotated with @Join");
                    }
                    if (!(simpleTypeName.contentEquals("Set") || simpleTypeName.contentEquals("List"))) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Recursive type must must be enclosed either Set or List");
                    }

                    // create new HashSet<>() for Set && new ArrayList<>() for List
                    expression.append("new java.util.");
                    if (simpleTypeName.contentEquals("Set")) {
                        expression.append("HashSet");
                    } else {
                        expression.append("ArrayList");
                    }
                    expression.append("<>()");

                    // recursively analize subtypes
                    for (var ta : dt.getTypeArguments()) {
                        var rcte = processingEnv.getTypeUtils().asElement(ta);
                        if (rcte.getKind() == ElementKind.RECORD) {
                            recursiveElements.add(rcte);
                        } else {
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Recursive type must must be only records");
                        }
                    }
                } else {
                    var typename = simpleTypeName.toString();
                    if (!DECLARED_TYPES.contains(typename)) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Not supported type: " + simpleTypeName);
                    }
                    switch (typename) {
                        case "Integer" -> {
                            expression.append(INTEGER_EXTRACTOR).append(rsIndex).append(")");
                        }
                        case "Long" -> {
                            expression.append(LONG_EXTRACTOR).append(rsIndex).append(")");
                        }
                        default -> {
                            expression.append("rs.get").append(typename).append("(").append(rsIndex).append(")");
                        }
                    }
                    ;
                }
            }
        } else if (typeKind == TypeKind.ARRAY) {
            if (rcType instanceof ArrayType at) {
                var arrayType = at.getComponentType();

                if (arrayType.getKind() != TypeKind.BYTE) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Not supported type for array: " + arrayType.toString() + "; must be only byte");
                }

                expression.append(BLOB_EXTRACTOR).append(rsIndex).append(")");
            }
        } else {
            // only TypeKind.BOOLEAN, TypeKind.INT, TypeKind.LONG TypeKind.DOUBLE
            if (!PRIMITIVE_TYPES.contains(typeKind)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Not supported primitive type, valid types are: int, long, boolean, double");
            }
            if (typeKind == TypeKind.BOOLEAN) {
                expression.append(BOOLEAN_EXTRACTOR);
            } else {
                expression.append("rs.get").append(capitalize(rcType.toString())).append("(");
            }
            expression.append(rsIndex).append(")");
        }
        return expression;
    }
}
