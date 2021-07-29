package com.acc.gluon.annotations;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.acc.gluon.annotations.GeneratorHelpers.printClass;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.acc.gluon.annotations.SQL")
@SupportedSourceVersion(SourceVersion.RELEASE_16)
public class SQLProcessor extends AbstractProcessor {
    private static final String PACKAGE_SUFFIX = ".impl";
    private static final String IMPL_CLASS_SUFFIX = "Impl";

    private static final List<String> IMPORTS = List.of("java.sql.SQLException", "com.acc.gluon.sql.SQLManager");

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (var e : roundEnv.getElementsAnnotatedWith(ResultSetConstructor.class)) {
            if (e.getKind() == ElementKind.INTERFACE) {
                processInterface(e);
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Element kind: " + e.getKind().name() + " is not a INTERFACE");
            }
        }
        return true;
    }

    private void processInterface(Element e) {
        var enclosingElement = e.getEnclosingElement();

        if (e instanceof TypeElement te && enclosingElement instanceof PackageElement pe) {
            String packagename = pe.getQualifiedName().toString();

            // check if interface extends AutoCloseable
            var autoCloseable = false;
            var interfaces = te.getInterfaces();
            for (var i : interfaces) {
                if (i.toString().equals("AutoCloseable")) {
                    autoCloseable = true;
                    break;
                }
            }

            if (!autoCloseable) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Interface: " + packagename + "." + e.getSimpleName() + "; kind: " + e.getKind().name() + " must implement AutoCloseable");
                return;
            }

            processInterface(packagename, te);
        } else {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Element: " + e.getSimpleName() + "; kind: " + e.getKind().name() + "; enclosing element: " + enclosingElement.getClass());
        }
    }

    private void processInterface(String packagename, TypeElement te) {
        String typename = te.getSimpleName().toString();

        try {
            JavaFileObject f = processingEnv
                    .getFiler()
                    .createSourceFile(packagename+ PACKAGE_SUFFIX + "." + typename + IMPL_CLASS_SUFFIX);

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Creating to " + f.toUri());

            try (Writer w = f.openWriter()) {
                PrintWriter pw = new PrintWriter(w);
                generateImplementation(pw, packagename, typename, te);
                pw.flush();
            }
        } catch (IOException x) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    x.toString());
        }
    }

    private void generateImplementation(PrintWriter pw, String packagename, String typename, TypeElement te) {
        String fqcn = packagename + "." + typename;

        var imports = new ArrayList<String>() {{
            addAll(IMPORTS);
            add(fqcn);
        }};

        printClass(pw, packagename + PACKAGE_SUFFIX, typename + IMPL_CLASS_SUFFIX, imports, () -> {
            // TODO: implements
        });

    }
}
