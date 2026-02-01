package xyz.srnyx.javautilities;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
     * Copies a resource folder to a target directory
     *
     * @param   resourcePath            the path to the resource folder (starting with {@code /})
     * @param   destinationDirectory    the target directory to copy the resource folder to
     *
     * @throws  IOException             if an I/O error occurs
     */
    public static void copyResourceFolder(@NotNull String resourcePath, @NotNull Path destinationDirectory) throws IOException {
        // Get resource
        final URL resource = FileUtility.class.getResource(resourcePath);
        if (resource == null) throw new FileNotFoundException("Resource not found: " + resourcePath);
        final URI uri;
        try {
            uri = resource.toURI();
        } catch (final URISyntaxException e) {
            throw new IOException("Invalid URI for resource: " + resourcePath, e);
        }

        // Create target directory if it doesn't exist
        if (!Files.exists(destinationDirectory)) Files.createDirectories(destinationDirectory);

        // Check if resource is a JAR file
        if (uri.getScheme().equals("jar")) {
            try (final FileSystem fileSystem = FileSystems.newFileSystem(uri, new HashMap<>())) {
                walkAndCopy(fileSystem.getPath(resourcePath), destinationDirectory);
            }
            return;
        }

        // Not a JAR file, treat as directory
        walkAndCopy(Paths.get(uri), destinationDirectory);
    }

    /**
     * Walks a source directory and copies all files and directories to a destination directory
     *
     * @param   source      the source directory
     * @param   destination the destination directory
     *
     * @throws  IOException if an I/O error occurs
     */
    public static void walkAndCopy(@NotNull Path source, @NotNull Path destination) throws IOException {
        try (final Stream<Path> files = Files.walk(source)) {
            files.forEach(path -> {
                try {
                    final Path fileDestination = destination.resolve(source.relativize(path).toString());

                    // Directory
                    if (Files.isDirectory(path)) {
                        Files.createDirectories(fileDestination);
                        return;
                    }

                    // File
                    Files.createDirectories(fileDestination.getParent());
                    Files.copy(path, fileDestination, StandardCopyOption.REPLACE_EXISTING);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Gets a {@link List} of all the file names in a specific resource folder
     *
     * @param   resourcePath    the path to the resource folder (starting without {@code /})
     *
     * @return                  the {@link List} of all the file names in the resource folder
     */
    @NotNull
    public static List<String> getResourceFiles(@NotNull String resourcePath) {
        if (!resourcePath.endsWith("/")) resourcePath += "/";
        final List<String> files = new ArrayList<>();

        try {
            final Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(resourcePath);
            while (resources.hasMoreElements()) {
                final URL url = resources.nextElement();
                final String protocol = url.getProtocol();

                // Handle file protocol
                if (protocol.equals("file")) {
                    try (final Stream<Path> stream = Files.list(Paths.get(url.toURI()))) {
                        stream.filter(Files::isRegularFile).forEach(p -> files.add(p.getFileName().toString()));
                    }
                    continue;
                }

                // Handle jar protocol
                if (protocol.equals("jar")) try (final JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile()) {
                    final Enumeration<JarEntry> entries = jar.entries();
                    final int resourcePathLength = resourcePath.length();
                    while (entries.hasMoreElements()) {
                        final JarEntry entry = entries.nextElement();
                        final String name = entry.getName();
                        if (!entry.isDirectory() && name.startsWith(resourcePath)) files.add(name.substring(resourcePathLength));
                    }
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return files;
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
