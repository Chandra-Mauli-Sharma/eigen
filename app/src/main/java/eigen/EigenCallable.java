package eigen;

import java.util.List;

interface EigenCallable {
    int arity();
    Object call(Compiler compiler, List<Object> arguments);
}
