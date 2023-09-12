package xyz.srnyx.javautilities;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * General utility methods for files
 */
public class FileUtility {
    /**
     * Deletes a file
     *
     * @param   path        the path to the file
     * @param   silentFail  if true, the error will be printed if the task fails
     */
    public static void deleteFile(@NotNull Path path, boolean silentFail) {
        try {
            Files.delete(path);
        } catch (final IOException e) {
            if (!silentFail) e.printStackTrace();
        }
    }

    /**
     * Gets a {@link Set} of all the names of the files in a folder with a given extension
     *
     * @param   file        the folder to get the files from
     * @param   extension   the extension of the files to get (without the first dot)
     *
     * @return              {@link Set} all the names of the files in the folder with the given extension
     */
    @NotNull
    public static Set<String> getFileNames(@NotNull File file, @NotNull String extension) {
        final File[] files = file.listFiles();
        if (files == null) return new HashSet<>();
        return Arrays.stream(files)
                .map(File::getName)
                .filter(name -> name.endsWith("." + extension))
                .map(name -> name.substring(0, name.length() - 4))
                .collect(Collectors.toSet());
    }

    /**
     * Constructs a new {@link FileUtility} instance (illegal)
     *
     * @throws  UnsupportedOperationException   if this class is instantiated
     */
    private FileUtility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
