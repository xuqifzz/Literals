import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import static java.util.Arrays.asList;
@SuppressWarnings("unchecked")
public class Literals {
    public static void main(String[] args) {

        Map<String,Object> m = hashMap(
                id -> 5,
                name -> "xuqi",
                age -> 18
        );
        System.out.println(m);
    }


    public static <T> HashMap<String, T> hashMap(MapEntry<T>... entries){
        return buildMap(new HashMap<>(),entries);
    }

    private  static <T, M extends Map<String, T> > M buildMap(M map, MapEntry<T>[] entries){
        Arrays.stream(entries).forEach(entry-> map.put(entry.name(),entry.value()));
        return map;
    }

    public static <T> T[] array(T...entries) {
        return entries;
    }

    public interface MapEntry<T> extends Serializable, Function<Void, T>{
        default String name() {
            try {
                Method replaceMethod = getClass().getDeclaredMethod("writeReplace");
                replaceMethod.setAccessible(true);
                SerializedLambda lambda= (SerializedLambda) replaceMethod.invoke(this);
                Class<?> containingClass = Class.forName(lambda.getImplClass().replaceAll("/","."));
                Method method = asList(containingClass.getDeclaredMethods())
                        .stream()
                        .filter(method0 -> Objects.equals(method0.getName(), lambda.getImplMethodName()))
                        .findFirst()
                        .orElseThrow(UnableToGuessMethodException::new);
                return method.getParameters()[0].getName();

            }catch (Exception ex){
                throw  new RuntimeException(ex);
            }

        }
        default T value(){
            return apply(null);
        }
        class UnableToGuessMethodException extends RuntimeException {}
    }
}
