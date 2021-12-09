package tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate <output directory>");
            System.exit(64);
        }
        var outputDir = args[0];
        defineAst(outputDir, "Expr",
                Arrays.asList("Assign   :Token name,Expr value", "Binary :Expr left,Token operator,Expr right",
                        "Call :Expr callee,Token paren,List<Expr> arguments", "Get :Expr object,Token name",
                        "Super :Token keyword,Token method", "Set :Expr object,Token name,Expr value",
                        "This :Token keyword", "Grouping :Expr expression",
                        "Logical :Expr left,Token operator,Expr right", "Literal  :Object value",
                        "Unary    :Token operator,Expr right", "Variable :Token name"));
        defineAst(outputDir, "Stmt",
                Arrays.asList("Block :List<Stmt> statements",
                        "Class :Token name,Expr.Variable superclass," + "List<Stmt.Function> methods",
                        "Expression :Expr expression", "Function :Token name,List<Token> params," + "List<Stmt> body",
                        "If :Expr condition,Stmt thenBranch," + "Stmt elseBranch", "Print :Expr expression",
                        "Return :Token keyword,Expr value", "Var :Token name,Expr initializer",
                        "While :Expr condition,Stmt body"));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        var path = outputDir + "/" + baseName + ".java";
        var writer = new PrintWriter(path, "UTF-8");

        writer.println("package eigen;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);
        for (var type : types) {
            var className = type.split(":")[0].trim();
            var fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }

        writer.println("    abstract <R> R accept(Visitor<R> visitor);");
        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> {");

        for (var type : types) {
            var typeName = type.split(":")[0].trim();

            writer.println("        R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println("    }");
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {

        writer.println("    static class " + className + " extends " + baseName + "{");

        var fields = fieldList.split(",");
        for (var field : fields) {
            writer.println("        final " + field + ";");
        }
        writer.println("        " + className + "(" + fieldList + ") {");

        for (var field : fields) {
            var name = field.split(" ")[1];

            writer.println("        this." + name + "=" + name + ";");
        }

        writer.println("    }");

        writer.println("  @Override");
        writer.println("  <R> R accept(Visitor<R> visitor) {");
        writer.println("      return visitor.visit" + className + baseName + "(this);");
        writer.println("    }");
        writer.println("  }");
    }
}
