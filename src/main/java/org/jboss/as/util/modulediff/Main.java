package org.jboss.as.util.modulediff;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Matej Lazar
 */
public class Main {


    // TODO ignore list
    /*
     * modules/sun/jdk/main
     * org/hornetq/main
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: Define two folder to compare.");
        }
        File moduleRoot1 = new File (args[0]);
        File moduleRoot2 = new File (args[1]);

        List<File> foldersToRemove = new Main().getFolderWithTheSameFiles(moduleRoot1, moduleRoot2);

        for (File folder : foldersToRemove) {
            System.out.println(folder);
        }
    }

    /**
     * Return list of folders that have the same files in root1 and root2
     *
     * @param moduleRoot1 original root
     * @param moduleRoot2 overly root
     */
    private List<File> getFolderWithTheSameFiles(File moduleRoot1, File moduleRoot2) {
        List<File> matchingFolders = new ArrayList<File>();
        List<File> leafFolders = getLeafFolders(moduleRoot1);
        for (File folder1 : leafFolders) {
            File folder2 = changeRoot(folder1, moduleRoot1, moduleRoot2);
            if (folder2 == null) {
                continue;
            }
            if (compareFolders(folder1, folder2)) {
                matchingFolders.add(folder1);
            }
        }
        return matchingFolders;
    }

    private boolean compareFolders(File folder1, File folder2) {
        List<String> files1 = Arrays.asList(folder1.list());
        List<String> files2 = Arrays.asList(folder2.list());

        if (files1.size() != files2.size()) {
            return false;
        }

        for (String fileName : files1) {
            if (files2.contains(fileName)) {
                if (compareFiles(new File(folder1, fileName), new File(folder2, fileName))) {
                    continue;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    private boolean compareFiles(File file1, File file2) {
        if (file1.length() != file2.length()) {
            return false;
        }

        return true;
    }

    /**
     * Change root of folder1 from moduleRoot1 to moduleRoot2
     */
    private File changeRoot(File folder1, File moduleRoot1, File moduleRoot2) {
        String relativePath = getRelativePath(folder1, moduleRoot1);
        File changed = new File(moduleRoot2.getAbsolutePath(), relativePath);
        if (changed.exists()) {
            return changed;
        }
        return null;
    }

    private String getRelativePath(File folder, File root) {
        return root.toURI().relativize(folder.toURI()).getPath();
    }

    /**
     * Recursive search for folders with no sub folders.
     */
    private List<File> getLeafFolders(File folder) {
        List<File> leafFolders = new ArrayList<File>();
        if (isFolderLeaf(folder)) {
            leafFolders.add(folder);
        } else {
            for (File file : folder.listFiles(new DirsOnlyFilter())) {
                leafFolders.addAll(getLeafFolders(file));
            }
            //TODO process non leaf folers with files
        }
        return leafFolders;
    }

    private boolean isFolderLeaf(File folder) {
        File[] folders = folder.listFiles(new DirsOnlyFilter());
        return folders.length == 0;
    }

}
