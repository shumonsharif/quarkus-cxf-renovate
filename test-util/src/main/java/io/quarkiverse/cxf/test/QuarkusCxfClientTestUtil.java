package io.quarkiverse.cxf.test;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.runtime.LaunchMode;

public class QuarkusCxfClientTestUtil {

    private QuarkusCxfClientTestUtil() {
    }

    public static <T> T getClient(Class<T> serviceInterface, String path) {
        try {
            final URL serviceUrl = new URL(getServerUrl() + path + "?wsdl");
            final QName qName = new QName(getDefaultNameSpace(serviceInterface), serviceInterface.getSimpleName());
            final Service service = javax.xml.ws.Service.create(serviceUrl, qName);
            return service.getPort(serviceInterface);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getServerUrl() {
        final Config config = ConfigProvider.getConfig();
        final int port = LaunchMode.current().equals(LaunchMode.TEST) ? config.getValue("quarkus.http.test-port", Integer.class)
                : config.getValue("quarkus.http.port", Integer.class);
        return String.format("http://localhost:%d", port);
    }

    public static String getEndpointUrl(Object port) {
        return (String) ((BindingProvider) port).getRequestContext().get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
    }

    public static String getDefaultNameSpace(Class<?> cl) {
        String pkg = cl.getName();
        int idx = pkg.lastIndexOf('.');
        if (idx != -1 && idx < pkg.length() - 1) {
            pkg = pkg.substring(0, idx);
        }
        String[] strs = pkg.split("\\.");
        StringBuilder b = new StringBuilder("http://");
        for (int i = strs.length - 1; i >= 0; i--) {
            if (i != strs.length - 1) {
                b.append(".");
            }
            b.append(strs[i]);
        }
        b.append("/");
        return b.toString();
    }
}
