package eigen;

import java.util.HashMap;
import java.util.Map;

public class EigenInstance {

    private EigenClass eigenClass;
    private final Map<String,Object> fields = new HashMap<>();
    public EigenInstance(EigenClass eigenClass) {
        this.eigenClass=eigenClass;
    }

    @Override
    public String toString() {
        return eigenClass.lexeme + "instance";
    }

    Object get(Token name){
        if(fields.containsKey(name.lexeme)){
            return fields.get(name.lexeme);
        }

        var method =eigenClass.findMethod(name.lexeme);
        if(method!=null){
            return method.bind(this);
        }
        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    void set(Token name,Object value){
        fields.put(name.lexeme,value);
    }
}
