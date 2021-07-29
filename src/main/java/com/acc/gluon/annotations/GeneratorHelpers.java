package com.acc.gluon.annotations;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class GeneratorHelpers {

    public static void printClass(PrintWriter writer, String packagename, String classname, Runnable bodyWriter) {
        generateClassHeader(writer, packagename, classname);
        bodyWriter.run();
        generateClassFooter(writer);
    }

    private static void generateClassHeader(PrintWriter pw, String packagename, String classname) {
        pw.println("package " + packagename + ";");
        pw.println();
        pw.println("public class " + classname + " {");
        pw.println();
    }

    private static void generateClassFooter(PrintWriter pw) {
        pw.println();
        pw.println("}");
    }

    public static void printMethod(PrintWriter writer, String name, String retType, boolean isPublic, boolean isStatic, List<String> arguments, StringBuilder body) {
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
                .append(") {");
        writer.print(header);

        // finalize function header
        writer.println(") {");
        // function body
        writer.println(body);
        // finalize function
        writer.println("    }");
    }
}
