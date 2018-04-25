package io.httpdoc.web;

import io.httpdoc.core.Document;
import io.httpdoc.core.Loader;
import io.httpdoc.core.Translation;
import io.httpdoc.core.Translator;
import io.httpdoc.core.exception.DocumentTranslationException;
import io.httpdoc.core.exception.HttpdocRuntimeException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * 缺省的翻译器
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-04-23 16:16
 **/
public class HttpdocMergedTranslator implements Translator {
    private final List<Translator> translators = new ArrayList<>();

    HttpdocMergedTranslator() {
        try {
            Set<URL> urls = Loader.load(HttpdocMergedTranslator.class.getClassLoader());
            for (URL url : urls) {
                if (!url.getFile().endsWith("/translator.properties")) continue;
                Properties properties = new Properties();
                properties.load(url.openStream());
                if (properties.isEmpty()) continue;
                for (Object value : properties.values()) {
                    String className = (String) value;
                    Translator translator = Class.forName(className).asSubclass(Translator.class).newInstance();
                    translators.add(translator);
                }
            }
        } catch (Exception e) {
            throw new HttpdocRuntimeException(e);
        }
    }

    @Override
    public Document translate(Translation translation) throws DocumentTranslationException {
        Document document = new Document();
        for (Translator translator : translators) {
            Document doc = translator.translate(translation);
            document.getControllers().addAll(doc.getControllers());
            document.getSchemas().putAll(doc.getSchemas());
        }
        return document;
    }

}