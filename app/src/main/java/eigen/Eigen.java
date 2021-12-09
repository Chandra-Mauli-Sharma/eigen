package eigen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Eigen {
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    private static final Compiler compiler = new Compiler();

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Use \"eigen file\"");
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runCLI();
        }
    }

    // run the file
    private static void runFile(String path) throws IOException {
        var bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError)
            System.exit(65);
        if (hadRuntimeError)
            System.exit(70);
    }

    private static void run(String charStream) throws IOException {
        var lexer = new Lexer(charStream);
        var tokens = lexer.scanTokens();

        var parser = new Parser(tokens);
        var statements = parser.parse();

        if (hadError)
            return;
        var resolver = new Resolver(compiler);
        resolver.resolve(statements);

        if (hadError)
            return;

        compiler.compile(statements);
    }

    // run code on CLI
    private static void runCLI() throws IOException {
        var input = new InputStreamReader(System.in);
        var reader = new BufferedReader(input);

        while (true) {
            System.out.print("##");
            var line = reader.readLine();
            if (line == null) {
                break;
            }
            run(line);
            hadError = false;
        }
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String location, String message) {
        System.err.println("[line " + line + "] Error" + location + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at " + token.lexeme + "'", message);
        }
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}
