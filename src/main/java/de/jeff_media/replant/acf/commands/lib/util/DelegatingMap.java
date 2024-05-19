package de.jeff_media.replant.acf.commands.lib.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DelegatingMap<K, V>
extends Map<K, V> {
    public Map<K, V> delegate(boolean var1);

    @Override
    default public int size() {
        return this.delegate(true).size();
    }

    @Override
    default public boolean isEmpty() {
        return this.delegate(true).isEmpty();
    }

    @Override
    default public boolean containsKey(Object key) {
        return this.delegate(true).containsKey(key);
    }

    @Override
    default public boolean containsValue(Object value) {
        return this.delegate(true).containsValue(value);
    }

    @Override
    default public V get(Object key) {
        return this.delegate(true).get(key);
    }

    @Override
    @Nullable
    default public V put(K key, V value) {
        return this.delegate(false).put(key, value);
    }

    @Override
    default public V remove(Object key) {
        return this.delegate(false).remove(key);
    }

    @Override
    default public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        this.delegate(false).putAll(m);
    }

    @Override
    default public void clear() {
        this.delegate(false).clear();
    }

    @Override
    @NotNull
    default public Set<K> keySet() {
        return this.delegate(false).keySet();
    }

    @Override
    @NotNull
    default public Collection<V> values() {
        return this.delegate(false).values();
    }

    @Override
    @NotNull
    default public Set<Map.Entry<K, V>> entrySet() {
        return this.delegate(false).entrySet();
    }

    @Override
    default public V getOrDefault(Object key, V defaultValue) {
        return this.delegate(true).getOrDefault(key, defaultValue);
    }

    @Override
    default public void forEach(BiConsumer<? super K, ? super V> action) {
        this.delegate(true).forEach(action);
    }

    @Override
    default public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        this.delegate(false).replaceAll(function);
    }

    @Override
    @Nullable
    default public V putIfAbsent(K key, V value) {
        return this.delegate(false).putIfAbsent(key, value);
    }

    @Override
    default public boolean remove(Object key, Object value) {
        return this.delegate(false).remove(key, value);
    }

    @Override
    default public boolean replace(K key, V oldValue, V newValue) {
        return this.delegate(false).replace(key, oldValue, newValue);
    }

    @Override
    @Nullable
    default public V replace(K key, V value) {
        return this.delegate(false).replace(key, value);
    }

    @Override
    default public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return this.delegate(false).computeIfAbsent((K)key, mappingFunction);
    }

    @Override
    default public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return this.delegate(false).computeIfPresent((K)key, (BiFunction<? super K, ? extends V, ? extends V>)remappingFunction);
    }

    @Override
    default public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return this.delegate(false).compute((K)key, (BiFunction<? super K, ? extends V, ? extends V>)remappingFunction);
    }

    @Override
    default public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return this.delegate(false).merge(key, (V)value, (BiFunction<? extends V, ? extends V, ? extends V>)remappingFunction);
    }
}

