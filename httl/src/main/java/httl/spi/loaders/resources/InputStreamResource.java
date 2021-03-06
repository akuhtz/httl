/*
 * Copyright 2011-2013 HTTL Team.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package httl.spi.loaders.resources;

import httl.Engine;
import httl.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.Locale;

/**
 * InputStreamResource. (SPI, Prototype, ThreadSafe)
 *
 * @author Liang Fei (liangfei0201 AT gmail DOT com)
 */
public abstract class InputStreamResource extends AbstractResource {

    protected static final String FILE_PROTOCOL = "file";
    protected static final String FILE_PROTOCOL_PREFIX = "file:";
    protected static final String JAR_PROTOCOL = "jar";
    protected static final String JAR_PROTOCOL_PREFIX = "jar:";
    protected static final String JAR_FILE_SEPARATOR = "!/";
    private static final long serialVersionUID = -5150738383353330217L;
    private final String path;

    public InputStreamResource(Engine engine, String name, Locale locale, String encoding, String path) {
        super(engine, name, locale, encoding);
        this.path = path;
    }

    public Reader openReader() throws IOException {
        InputStream in = openStream();
        if (in == null) {
            throw new FileNotFoundException("Not found template " + getName() + " in " + getClass().getSimpleName() + ": " + getPath());
        }
        String encoding = getEncoding();
        return StringUtils.isEmpty(encoding)
                ? new InputStreamReader(in) : new InputStreamReader(in, encoding);
    }

    public long getLastModified() {
        File file = getFile();
        if (file != null && file.exists()) {
            return file.lastModified();
        }
        URL url = getUrl();
        if (url != null) {
            if (JAR_PROTOCOL.equals(url.getProtocol())) {
                String path = url.getFile();
                if (path.startsWith(JAR_PROTOCOL_PREFIX)) {
                    path = path.substring(JAR_PROTOCOL_PREFIX.length());
                }
                if (path.startsWith(FILE_PROTOCOL_PREFIX)) {
                    path = path.substring(FILE_PROTOCOL_PREFIX.length());
                }
                int i = path.indexOf(JAR_FILE_SEPARATOR);
                if (i > 0) {
                    path = path.substring(0, i);
                }
                file = new File(path);
                if (file.exists()) {
                    return file.lastModified();
                }
            }
        }
        return super.getLastModified();
    }

    public long getLength() {
        File file = getFile();
        if (file != null) {
            return file.length();
        }
        try {
            InputStream in = openStream();
            if (in != null) {
                try {
                    return in.available();
                } finally {
                    in.close();
                }
            }
        } catch (IOException e) {
        }
        return super.getLength();
    }

    public File getFile() {
        URL url = getUrl();
        if (url != null) {
            if (FILE_PROTOCOL.equals(url.getProtocol())) {
                String path = url.getFile();
                if (path.startsWith(FILE_PROTOCOL_PREFIX)) {
                    path = path.substring(FILE_PROTOCOL_PREFIX.length());
                }
                File file = new File(path);
                if (file.exists()) {
                    return file;
                }
            }
        }
        return null;
    }

    protected URL getUrl() {
        return null;
    }

    protected String getPath() {
        return path;
    }

}