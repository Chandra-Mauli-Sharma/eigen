package eigen;

import java.util.List;

public class EigenFunction implements EigenCallable {
    private final Stmt.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;

    public EigenFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
        this.isInitializer = isInitializer;
        this.closure = closure;
        this.declaration = declaration;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public String toString() {
        return "<function" + declaration.name.lexeme + ">";
    }

    @Override
    public Object call(Compiler compiler, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        try {
            compiler.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            if (isInitializer) {
                return closure.getAt(0, "this");
            }
            return returnValue.value;
        }

        if (isInitializer) {
            return closure.getAt(0, "this");
        }
        return null;
    }

    public EigenFunction bind(EigenInstance eigenInstance) {
        Environment environment = new Environment(closure);
        environment.define("this", eigenInstance);
        return new EigenFunction(declaration, environment, isInitializer);
    }

}
