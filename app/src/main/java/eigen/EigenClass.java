package eigen;

import java.util.List;
import java.util.Map;

public class EigenClass implements EigenCallable {

    final String lexeme;
    final private EigenClass superclass;
    private final Map<String, EigenFunction> methods;

    public EigenClass(String lexeme, EigenClass superclass, Map<String, EigenFunction> methods) {
        this.lexeme = lexeme;
        this.superclass = superclass;
        this.methods = methods;
    }

    @Override
    public String toString() {
        return lexeme;
    }

    @Override
    public int arity() {
        var initializer = findMethod("init");
        if (initializer == null) {
            return 0;
        }
        return initializer.arity();
    }

    @Override
    public Object call(Compiler compiler, List<Object> arguments) {
        var instance = new EigenInstance(this);
        var initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(compiler, arguments);
        }
        return instance;
    }

    EigenFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }
        if (superclass != null) {
            return superclass.findMethod(name);
        }
        return null;
    }
}
