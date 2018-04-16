package io.httpdoc.core.description;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface Describer {

    String describe(Class<?> clazz);

    String describe(Method method);

    String describe(Field field);

    String describe(Enum<?> constant);

    String describe(PropertyDescriptor descriptor);

}
