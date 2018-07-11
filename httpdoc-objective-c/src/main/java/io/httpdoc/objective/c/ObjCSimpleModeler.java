package io.httpdoc.objective.c;

import io.httpdoc.core.Category;
import io.httpdoc.core.Constant;
import io.httpdoc.core.Property;
import io.httpdoc.core.Schema;
import io.httpdoc.core.exception.SchemaDesignException;
import io.httpdoc.core.fragment.CommentFragment;
import io.httpdoc.core.fragment.ConstantFragment;
import io.httpdoc.core.fragment.FieldFragment;
import io.httpdoc.core.modeler.Archetype;
import io.httpdoc.core.modeler.Modeler;
import io.httpdoc.core.supplier.Supplier;
import io.httpdoc.core.type.HDClass;
import io.httpdoc.core.type.HDType;
import io.httpdoc.objective.c.fragment.ObjCClassFragment;

import java.util.*;

/**
 * 简单的模型师
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-05-18 11:15
 **/
public class ObjCSimpleModeler implements Modeler<ObjCClassFragment> {

    @Override
    public Collection<ObjCClassFragment> design(Archetype archetype) throws SchemaDesignException {
        final String comment = "Generated By Httpdoc";
        final String pkgGenerated = archetype.getPkg();
        final boolean pkgForced = archetype.isPkgForced();
        final Supplier supplier = archetype.getSupplier();
        final Schema schema = archetype.getSchema();
        final String pkgTranslated = schema.getPkg();
        final String pkg = pkgForced || pkgTranslated == null ? pkgGenerated : pkgTranslated;
        final String name = schema.getName();
        switch (schema.getCategory()) {
            case ENUM:
                ObjCClassFragment enumeration = new ObjCClassFragment();
                enumeration.setPkg(pkg);
                enumeration.setCommentFragment(new CommentFragment(schema.getDescription() != null ? schema.getDescription() + "\n" + comment : comment));
                enumeration.setClazz(new HDClass(HDClass.Category.ENUM, (pkg == null || pkg.isEmpty() ? "" : pkg + ".") + name));
                Set<Constant> constants = schema.getConstants();
                for (Constant constant : (constants != null ? constants : Collections.<Constant>emptySet())) {
                    ConstantFragment con = new ConstantFragment(new CommentFragment(constant.getDescription()), constant.getName());
                    enumeration.getConstantFragments().add(con);
                }
                return Collections.singleton(enumeration);
            case OBJECT:
                ObjCClassFragment interfase = new ObjCClassFragment();
                interfase.setPkg(pkg);
                interfase.setCommentFragment(new CommentFragment(schema.getDescription() != null ? schema.getDescription() + "\n" + comment : comment));
                interfase.setClazz(new HDClass(HDClass.Category.INTERFACE, (pkg == null || pkg.isEmpty() ? "" : pkg + ".") + name));
                Schema superclass = schema.getSuperclass();
                interfase.setSuperclass(superclass != null && superclass.getCategory() == Category.OBJECT ? superclass.toType(pkgGenerated, pkgForced, supplier) : null);
                Map<String, Property> properties = schema.getProperties();
                for (Map.Entry<String, Property> entry : (properties != null ? properties.entrySet() : Collections.<Map.Entry<String, Property>>emptySet())) {
                    Property property = entry.getValue();
                    HDType type = property.getType().toType(pkgGenerated, pkgForced, supplier);
                    FieldFragment field = new FieldFragment();
                    field.setName(entry.getKey());
                    field.setType(type);
                    field.setCommentFragment(new CommentFragment(property.getDescription()));
                    interfase.getFieldFragments().add(field);
                }

                ObjCClassFragment implementation = new ObjCClassFragment();
                implementation.setPkg(pkg);
                implementation.setCommentFragment(new CommentFragment(schema.getDescription() != null ? schema.getDescription() + "\n" + comment : comment));
                implementation.setClazz(new HDClass(HDClass.Category.CLASS, (pkg == null || pkg.isEmpty() ? "" : pkg + ".") + name));

                return Arrays.asList(interfase, implementation);
            default:
                return Collections.emptySet();
        }
    }

}
