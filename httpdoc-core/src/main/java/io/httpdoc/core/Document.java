package io.httpdoc.core;

import java.util.List;
import java.util.Map;

/**
 * 文档
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-04-12 19:06
 **/
public class Document extends Definition {
    private static final long serialVersionUID = 4240537886514527060L;

    private String httpdoc;
    private String protocol;
    private String hostname;
    private String ctxtpath;
    private String version;
    private List<Controller> controllers;
    private Map<String, Schema> schemas;

    public String getHttpdoc() {
        return httpdoc;
    }

    public void setHttpdoc(String httpdoc) {
        this.httpdoc = httpdoc;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getCtxtpath() {
        return ctxtpath;
    }

    public void setCtxtpath(String ctxtpath) {
        this.ctxtpath = ctxtpath;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Controller> getControllers() {
        return controllers;
    }

    public void setControllers(List<Controller> controllers) {
        this.controllers = controllers;
    }

    public Map<String, Schema> getSchemas() {
        return schemas;
    }

    public void setSchemas(Map<String, Schema> schemas) {
        this.schemas = schemas;
    }

}
