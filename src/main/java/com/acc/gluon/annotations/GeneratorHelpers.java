package com.acc.gluon.annotations;

import java.io.PrintWriter;
import java.util.List;

public class GeneratorHelpers {

    public static void printClass(PrintWriter writer, String packagename, String classname, List<String> imports, Runnable bodyWriter) {
        generateClassHeader(writer, packagename, classname, imports);
        bodyWriter.run();
        generateClassFooter(writer);
    }

    private static void generateClassHeader(PrintWriter pw, String packagename, String classname, List<String> imports) {
        pw.println("package " + packagename + ";");
        pw.println();
        for (var imp : imports) {
            pw.println("import " + imp);
        }
        pw.println();
        pw.println("public class " + classname + " {");
        pw.println();
    }

    private static void generateClassFooter(PrintWriter pw) {
        pw.println();
        pw.println("}");
    }

    public static void printMethod(PrintWriter writer, String name, String retType, boolean isPublic, boolean isStatic, List<String> arguments, List<String> exceptions, StringBuilder body) {
        StringBuilder header = new StringBuilder("    ");
        if (isPublic) {
            header.append("public ");
        }
        if (isStatic) {
            header.append("static ");
        }
        header
                .append(retType).append(" ")
                .append(name).append("(")
                .append(String.join(",", arguments))
                .append(") ");

        if (exceptions.size() > 0) {
            header.append(" throws ").append(String.join(" ", exceptions));
        }

        // finalize function header
        header.append("{");
        writer.print(header);

        // function body
        writer.println(body);
        // finalize function
        writer.println("        }");
    }
}
