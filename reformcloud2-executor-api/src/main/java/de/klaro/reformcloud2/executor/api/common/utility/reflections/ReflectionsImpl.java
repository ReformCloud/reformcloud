package de.klaro.reformcloud2.executor.api.common.utility.reflections;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This class overrides the
 * {@link Reflections#getSubTypesOf(Class)} &
 * {@link Reflections#expandSuperTypes()}
 * methods to prevent issues with the spigot api (old guava impl of {@link Sets.SetView#iterator()})
 */
public final class ReflectionsImpl extends Reflections {

    public ReflectionsImpl(String prefix, Scanner... scanners) {
        super(prefix, scanners);
    }

    @Override
    public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type) {
        List<Class<? extends T>> list = ReflectionUtils.forNames(
                store.getAll(SubTypesScanner.class.getSimpleName(), Collections.singletonList(type.getName())), configuration.getClassLoaders()
        );
        return new HashSet<>(list);
    }

    @Override
    public void expandSuperTypes() {
        if (this.store.keySet().contains(SubTypesScanner.class.getSimpleName())) {
            Multimap<String, String> mmap = this.store.get(SubTypesScanner.class.getSimpleName());
            Set<String> keys = diff(mmap.keySet(), new HashSet<>(mmap.values()));
            Multimap<String, String> expand = HashMultimap.create();

            for (String key : keys) {
                Class<?> type = ReflectionUtils.forName(key);
                if (type != null) {
                    this.expandSupertypes(expand, key, type);
                }
            }

            mmap.putAll(expand);
        }
    }

    private void expandSupertypes(Multimap<String, String> mmap, String key, Class<?> type) {
        for (Class<?> aClass : ReflectionUtils.getSuperTypes(type)) {
            if (mmap.put(aClass.getName(), key)) {
                this.expandSupertypes(mmap, aClass.getName(), aClass);
            }
        }
    }

    private <E> Set<E> diff(final Set<E> set1, final Set<?> set2) {
        Preconditions.checkNotNull(set1);
        Preconditions.checkNotNull(set2);

        Set<E> out = new HashSet<>();
        out.addAll(Links.allOf(set1, new Predicate<E>() {
            @Override
            public boolean test(E e) {
                return !set2.contains(e);
            }
        }));
        return out;
    }
}
