package xyz.srnyx.javautilities.parents;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;


/**
 * Represents an object that can be converted to a {@link String} via {@link #toString()}
 */
public class Stringable {
    @Override @NotNull
    public String toString() {
        return toString(this);
    }

    /**
     * Creates a new {@link Stringable} object (shouldn't be used)
     */
    public Stringable() {
        // Only exists to give the constructor a Javadoc
    }

    /**
     * Converts an object to a {@link String} by getting all of its fields and their values based on the given predicate
     * <br>Will also get superclass fields
     *
     * @param   object          the object to convert
     * @param   fieldPredicate  the predicate to test the fields with (if null, all fields are included)
     *
     * @return                  the object as a {@link String}
     */
    @NotNull
    public static String toString(@Nullable Object object, @Nullable Predicate<Field> fieldPredicate) {
        if (object == null) return "null";
        final Class<?> clazz = object.getClass();

        // Get declared fields and fields from all superclasses
        final List<Field> fields = new ArrayList<>();
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) fields.addAll(Arrays.asList(c.getDeclaredFields()));

        // Create the string
        final boolean fieldPredicateNull = fieldPredicate == null;
        return clazz.getSimpleName() + "{" + fields.stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()) && (fieldPredicateNull || fieldPredicate.test(field)))
                .map(field -> {
                    final String entry;
                    try {
                        final boolean inaccessible = !field.isAccessible();
                        if (inaccessible) field.setAccessible(true);
                        entry = field.getName() + "='" + field.get(object) + "'";
                        if (inaccessible) field.setAccessible(false);
                    } catch (final Exception e) {
                        return null;
                    }
                    return entry;
                })
                .filter(Objects::nonNull)
                .reduce((a, b) -> a + ", " + b)
                .orElse("") + "}";
    }

    /**
     * Converts an object to a {@link String} by getting all of its fields and their values, excluding fields with the given classes
     *
     * @param   object          the object to convert
     * @param   ignoredClasses  the classes to exclude from the conversion
     *
     * @return                  the object as a {@link String}
     */
    @NotNull
    public static String toString(@Nullable Object object, @NotNull Class<?>... ignoredClasses) {
        final Set<Class<?>> ignoredClassesSet = new HashSet<>(Arrays.asList(ignoredClasses));
        return toString(object, field -> !ignoredClassesSet.contains(field.getType()));
    }

    /**
     * Converts an object to a {@link String} by getting all of its fields and their values, excluding fields with the given names
     *
     * @param   object          the object to convert
     * @param   ignoredFields   the names of the fields to exclude from the conversion
     *
     * @return                  the object as a {@link String}
     */
    @NotNull
    public static String toString(@Nullable Object object, @NotNull String... ignoredFields) {
        final Set<String> ignoredFieldsSet = new HashSet<>(Arrays.asList(ignoredFields));
        return toString(object, field -> !ignoredFieldsSet.contains(field.getName()));
    }

    /**
     * Converts an object to a {@link String} by getting all of its fields and their values
     *
     * @param   object  the object to convert
     *
     * @return          the object as a {@link String}
     */
    @NotNull
    public static String toString(@Nullable Object object) {
        return toString(object, (Predicate<Field>) null);
    }
}
