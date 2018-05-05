package io.httpdoc.core.type;

import java.util.Collections;
import java.util.List;

import static io.httpdoc.core.type.HDClass.Category.*;

/**
 * 类型
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-05-02 12:18
 **/
public class HDClass extends HDType {
    private final Category category;
    private final String name;
    private final HDClass component;
    private HDTypeVariable[] typeParameters;

    public HDClass(String name) {
        if (name == null) throw new NullPointerException();
        this.name = name;
        this.component = name.endsWith("[]") ? new HDClass(name.substring(0, name.length() - 2)) : null;
        this.category = this.component != null ? ARRAY : Category.CLASS;
    }

    public HDClass(Category category, String name) {
        if (category == null || name == null) throw new NullPointerException();
        this.name = name;
        this.component = name.endsWith("[]") ? new HDClass(category, name.substring(0, name.length() - 2)) : null;
        this.category = this.component != null ? ARRAY : category;
    }

    public HDClass(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        } else if (clazz.isArray()) {
            category = ARRAY;
            name = (component = new HDClass(clazz.getComponentType())).getName() + "[]";
        } else {
            category = clazz.isInterface() ? INTERFACE : clazz.isAnnotation() ? ANNOTATION : clazz.isEnum() ? ENUM : CLASS;
            name = clazz.getName();
            component = null;
        }
    }

    @Override
    public List<String> imports() {
        return component != null ? component.imports() : Collections.singletonList(name);
    }

    public String getSimpleName() {
        return name.contains(".") ? name.substring(name.lastIndexOf(".") + 1) : name;
    }

    @Override
    public CharSequence getFormatName() {
        return component != null ? component.getFormatName() + "[]" : getSimpleName();
    }

    @Override
    public int length() {
        return getFormatName().length();
    }

    @Override
    public char charAt(int index) {
        return getFormatName().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return getFormatName().subSequence(start, end);
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public HDClass getComponent() {
        return component;
    }

    public HDTypeVariable[] getTypeParameters() {
        return typeParameters;
    }

    void setTypeParameters(HDTypeVariable[] typeParameters) {
        this.typeParameters = typeParameters;
    }

    public enum Category {
        CLASS("class"), INTERFACE("interface"), ANNOTATION("@interface"), ENUM("enum"), ARRAY("array");

        public final String name;

        Category(String name) {
            this.name = name;
        }

    }

}