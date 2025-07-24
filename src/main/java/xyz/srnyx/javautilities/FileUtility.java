package xyz.srnyx.javautilities;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
     *
     * @return              true if the file was deleted, false otherwise
     */
    public static boolean deleteFile(@NotNull Path path, boolean silentFail) {
        try {
            Files.delete(path);
            return true;
        } catch (final IOException e) {
            if (!silentFail) e.printStackTrace();
            return false;
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
        if (files == null) return Collections.emptySet();
        final String fullExtension = "." + extension;
        final int extensionLength = fullExtension.length();
        return Arrays.stream(files)
                .map(File::getName)
                .filter(name -> name.endsWith(fullExtension))
                .map(name -> name.substring(0, name.length() - extensionLength))
                .collect(Collectors.toSet());
    }

    /**
     * Get the content of a resource file as a {@link List} of {@link String}s
     *
     * @param   fileName    the name of the resource file
     *
     * @return              the content of the resource file
     */
    @NotNull
    public static List<String> getResourceContent(@NotNull String fileName) {
        try (final InputStream inputStream = FileUtility.class.getResourceAsStream(fileName)) {
            if (inputStream == null) throw new NullPointerException("Failed to get resource: " + fileName);
            final List<String> content = new ArrayList<>();
            try (final Scanner scanner = new Scanner(inputStream)) {
                while (scanner.hasNextLine()) content.add(scanner.nextLine());
            } catch (final NullPointerException e) {
                e.printStackTrace();
            }
            return content;
        } catch (final IOException e) {
            throw new RuntimeException("Failed to read resource: " + fileName, e);
        }
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
