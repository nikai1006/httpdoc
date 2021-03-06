package io.httpdoc.jestful.client;

import io.httpdoc.core.Controller;
import io.httpdoc.core.Operation;
import io.httpdoc.core.Parameter;
import io.httpdoc.core.fragment.ClassFragment;
import io.httpdoc.core.fragment.CommentFragment;
import io.httpdoc.core.fragment.MethodFragment;
import io.httpdoc.core.fragment.ParameterFragment;
import io.httpdoc.core.fragment.annotation.HDAnnotation;
import io.httpdoc.core.fragment.annotation.HDAnnotationConstant;
import io.httpdoc.core.generation.*;
import io.httpdoc.core.kit.StringKit;
import io.httpdoc.core.modeler.Modeler;
import io.httpdoc.core.modeler.SimpleModeler;
import io.httpdoc.core.supplier.Supplier;
import io.httpdoc.core.type.HDClass;
import org.qfox.jestful.core.http.*;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import static io.httpdoc.core.Parameter.*;

/**
 * Jestful Client 生成器
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-04-27 15:59
 **/
public abstract class JestfulAbstractGenerator extends FragmentGenerator implements Generator {
    protected final String prefix;
    protected final String suffix;

    protected JestfulAbstractGenerator() {
        this("", "");
    }

    protected JestfulAbstractGenerator(Modeler<ClassFragment> modeler) {
        this(modeler, "", "");
    }

    protected JestfulAbstractGenerator(String prefix, String suffix) {
        this(new SimpleModeler(), prefix, suffix);
    }

    protected JestfulAbstractGenerator(Modeler<ClassFragment> modeler, String prefix, String suffix) {
        super(modeler);
        if (prefix == null || suffix == null) throw new NullPointerException();
        this.prefix = prefix.trim();
        this.suffix = suffix.trim();
    }

    protected Collection<ClassFragment> generate(ControllerGenerateContext context) {
        Generation generation = context.getGeneration();
        String pkgGenerated = context.getPkg();
        boolean pkgForced = context.isPkgForced();
        Controller controller = context.getController();
        String comment = "Generated By Httpdoc";
        String name = controller.getName();
        ClassFragment interfase = new ClassFragment();
        String pkgTranslated = controller.getPkg();
        String pkg = pkgForced || pkgTranslated == null ? pkgGenerated : pkgTranslated;
        interfase.setPkg(pkg);
        interfase.setCommentFragment(new CommentFragment(controller.getDescription() != null ? controller.getDescription() + "\n" + comment : comment));
        interfase.setClazz(new HDClass(HDClass.Category.INTERFACE, pkg + "." + name));

        HDAnnotation http = new HDAnnotation(HTTP.class);
        http.getProperties().put("value", HDAnnotationConstant.valuesOf(controller.getPath() != null ? controller.getPath() : ""));
        interfase.getAnnotations().add(http);

        List<Operation> operations = controller.getOperations() != null ? controller.getOperations() : Collections.<Operation>emptyList();
        for (Operation operation : operations) {
            Collection<MethodFragment> methods = generate(new OperationGenerateContext(generation, controller, operation));
            if (methods == null) continue;
            interfase.getMethodFragments().addAll(methods);
        }

        return Collections.singleton(interfase);
    }

    protected abstract Collection<MethodFragment> generate(OperationGenerateContext context);

    protected Collection<ParameterFragment> generate(ParameterGenerateContext context) {
        String pkg = context.getPkg();
        boolean pkgForced = context.isPkgForced();
        Supplier supplier = context.getSupplier();
        List<Parameter> parameters = context.getParameters();
        Collection<ParameterFragment> fragments = new LinkedHashSet<>();
        for (int i = 0; parameters != null && i < parameters.size(); i++) {
            Parameter parameter = parameters.get(i);
            ParameterFragment fragment = new ParameterFragment();
            String name = StringKit.isBlank(parameter.getName()) ? parameter.getType().toName() : parameter.getName();
            // 去掉特殊字符
            name = name.replaceAll("[^0-9a-zA-Z_$]", "_");
            loop:
            while (true) {
                for (ParameterFragment prev : fragments) {
                    if (name.equals(prev.getName())) {
                        name = String.format("_%s", name);
                        continue loop;
                    }
                }
                break;
            }
            fragment.setName(name);
            fragment.setComment(parameter.getDescription());
            Collection<HDAnnotation> annotations = annotate(parameter);
            fragment.getAnnotations().addAll(annotations);
            fragment.setType(parameter.getType().toType(pkg, pkgForced, supplier));
            fragments.add(fragment);
        }
        return fragments;
    }

    protected String name(String name) {
        if (prefix.isEmpty()) return name + suffix;
        else return prefix + name.substring(0, 1).toUpperCase() + name.substring(1) + suffix;
    }

    protected Collection<HDAnnotation> annotate(Parameter parameter) {
        String name = parameter.getName();
        switch (parameter.getScope()) {
            case HTTP_PARAM_SCOPE_HEADER: {
                HDAnnotation header = new HDAnnotation(Header.class);
                if (name != null)
                    header.getProperties().put("value", HDAnnotationConstant.valuesOf(name.isEmpty() ? "*" : name));
                return Collections.singleton(header);
            }
            case HTTP_PARAM_SCOPE_PATH: {
                HDAnnotation path = new HDAnnotation(Path.class);
                if (name != null)
                    path.getProperties().put("value", HDAnnotationConstant.valuesOf(name.isEmpty() ? "*" : name));
                return Collections.singleton(path);
            }
            case HTTP_PARAM_SCOPE_MATRIX: {
                HDAnnotation matrix = new HDAnnotation(Matrix.class);
                if (name != null)
                    matrix.getProperties().put("value", HDAnnotationConstant.valuesOf(name.isEmpty() ? "*" : name));
                String path = parameter.getPath();
                if (path != null)
                    matrix.getProperties().put("path", HDAnnotationConstant.valuesOf(path.isEmpty() ? "" : path));
                return Collections.singleton(matrix);
            }
            case HTTP_PARAM_SCOPE_QUERY: {
                HDAnnotation query = new HDAnnotation(Query.class);
                if (name != null)
                    query.getProperties().put("value", HDAnnotationConstant.valuesOf(name.isEmpty() ? "*" : name));
                return Collections.singleton(query);
            }
            case HTTP_PARAM_SCOPE_BODY: {
                HDAnnotation body = new HDAnnotation(Body.class);
                if (name != null)
                    body.getProperties().put("value", HDAnnotationConstant.valuesOf(name.isEmpty() ? "*" : name));
                return Collections.singleton(body);
            }
            case HTTP_PARAM_SCOPE_COOKIE: {
                HDAnnotation cookie = new HDAnnotation(Cookie.class);
                if (name != null)
                    cookie.getProperties().put("value", HDAnnotationConstant.valuesOf(name.isEmpty() ? "*" : name));
                return Collections.singleton(cookie);
            }
            case HTTP_PARAM_SCOPE_FIELD: {
                HDAnnotation query = new HDAnnotation(Query.class);
                if (name != null)
                    query.getProperties().put("value", HDAnnotationConstant.valuesOf(name.isEmpty() ? "*" : name));
                return Collections.singleton(query);
            }
            default: {
                return Collections.emptyList();
            }
        }
    }

    protected Collection<HDAnnotation> annotate(Operation operation) {
        switch (operation.getMethod()) {
            case "HEAD": {
                HDAnnotation head = new HDAnnotation(HEAD.class);
                if (operation.getPath() != null)
                    head.getProperties().put("value", HDAnnotationConstant.valuesOf(operation.getPath()));
                if (operation.getProduces() != null)
                    head.getProperties().put("produces", HDAnnotationConstant.valuesOf(operation.getProduces().toArray(new Object[0])));
                return Collections.singleton(head);
            }
            case "OPTIONS": {
                HDAnnotation options = new HDAnnotation(OPTIONS.class);
                if (operation.getPath() != null)
                    options.getProperties().put("value", HDAnnotationConstant.valuesOf(operation.getPath()));
                if (operation.getProduces() != null)
                    options.getProperties().put("produces", HDAnnotationConstant.valuesOf(operation.getProduces().toArray(new Object[0])));
                return Collections.singleton(options);
            }
            case "GET": {
                HDAnnotation get = new HDAnnotation(GET.class);
                if (operation.getPath() != null)
                    get.getProperties().put("value", HDAnnotationConstant.valuesOf(operation.getPath()));
                if (operation.getProduces() != null)
                    get.getProperties().put("produces", HDAnnotationConstant.valuesOf(operation.getProduces().toArray(new Object[0])));
                return Collections.singleton(get);
            }
            case "POST": {
                HDAnnotation post = new HDAnnotation(POST.class);
                if (operation.getPath() != null)
                    post.getProperties().put("value", HDAnnotationConstant.valuesOf(operation.getPath()));
                if (operation.getProduces() != null)
                    post.getProperties().put("produces", HDAnnotationConstant.valuesOf(operation.getProduces().toArray(new Object[0])));
                if (operation.getConsumes() != null)
                    post.getProperties().put("consumes", HDAnnotationConstant.valuesOf(operation.getConsumes().toArray(new Object[0])));
                return Collections.singleton(post);
            }
            case "PUT": {
                HDAnnotation put = new HDAnnotation(PUT.class);
                if (operation.getPath() != null)
                    put.getProperties().put("value", HDAnnotationConstant.valuesOf(operation.getPath()));
                if (operation.getProduces() != null)
                    put.getProperties().put("produces", HDAnnotationConstant.valuesOf(operation.getProduces().toArray(new Object[0])));
                if (operation.getConsumes() != null)
                    put.getProperties().put("consumes", HDAnnotationConstant.valuesOf(operation.getConsumes().toArray(new Object[0])));
                return Collections.singleton(put);
            }
            case "DELETE": {
                HDAnnotation delete = new HDAnnotation(DELETE.class);
                if (operation.getPath() != null)
                    delete.getProperties().put("value", HDAnnotationConstant.valuesOf(operation.getPath()));
                if (operation.getProduces() != null)
                    delete.getProperties().put("produces", HDAnnotationConstant.valuesOf(operation.getProduces().toArray(new Object[0])));
                return Collections.singleton(delete);
            }
            default: {
                return Collections.emptyList();
            }
        }
    }

}
