package org.jboss.as.util.modulediff;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Matej Lazar
 */
public class FilesOnlyFilter implements FileFilter {

    @Override
    public boolean accept(File file) {
        return file.isFile();
    }

}
