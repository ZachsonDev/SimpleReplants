package de.jeff_media.replant.acf.commands.lib.util;

import de.jeff_media.replant.acf.commands.lib.util.DelegatingMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.Nullable;

public class Table<R, C, V>
implements Iterable<Entry<R, C, V>> {
    private final Map<R, Map<C, V>> rowMap;
    private final Function<R, Map<C, V>> colMapSupplier;

    public Table() {
        this(new HashMap(), HashMap::new);
    }

    public Table(Supplier<Map<C, V>> supplier) {
        this(new HashMap(), supplier);
    }

    public Table(Map<R, Map<C, V>> map, Supplier<Map<C, V>> supplier) {
        this(map, (R object) -> (Map)supplier.get());
    }

    public Table(Map<R, Map<C, V>> map, Function<R, Map<C, V>> function) {
        this.rowMap = map;
        this.colMapSupplier = function;
    }

    public V get(R r, C c) {
        return this.getIfExists(r, c);
    }

    public V getOrDefault(R r, C c, V v) {
        Map<C, V> map = this.getColMapIfExists(r);
        if (map == null) {
            return v;
        }
        V v2 = map.get(c);
        if (v2 != null || map.containsKey(c)) {
            return v2;
        }
        return v;
    }

    public boolean containsKey(R r, C c) {
        Map<C, V> map = this.getColMapIfExists(r);
        if (map == null) {
            return false;
        }
        return map.containsKey(c);
    }

    @Nullable
    public V put(R r, C c, V v) {
        return this.getColMapForWrite(r).put(c, v);
    }

    public void forEach(TableConsumer<R, C, V> tableConsumer) {
        for (Entry<R, C, V> entry : this) {
            tableConsumer.accept(entry.getRow(), entry.getCol(), entry.getValue());
        }
    }

    public void forEach(TablePredicate<R, C, V> tablePredicate) {
        for (Entry<R, C, V> entry : this) {
            if (tablePredicate.test(entry.getRow(), entry.getCol(), entry.getValue())) continue;
            return;
        }
    }

    public void removeIf(TablePredicate<R, C, V> tablePredicate) {
        Iterator<Entry<R, C, V>> iterator = this.iterator();
        while (iterator.hasNext()) {
            Entry<R, C, V> entry = iterator.next();
            if (!tablePredicate.test(entry.getRow(), entry.getCol(), entry.getValue())) continue;
            iterator.remove();
        }
    }

    public Stream<Entry<R, C, V>> stream() {
        return this.stream(false);
    }

    public Stream<Entry<R, C, V>> stream(boolean bl) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.iterator(), 0), bl);
    }

    @Override
    public Iterator<Entry<R, C, V>> iterator() {
        return new Iterator<Entry<R, C, V>>(){
            Iterator<Map.Entry<R, Map<C, V>>> rowIter;
            Iterator<Map.Entry<C, V>> colIter;
            private Map.Entry<R, Map<C, V>> rowEntry;
            private Map.Entry<C, V> colEntry;
            private Entry<R, C, V> next;
            {
                this.rowIter = Table.this.rowMap.entrySet().iterator();
                this.colIter = null;
                this.next = this.getNext();
            }

            private Entry<R, C, V> getNext() {
                if (this.colIter == null || !this.colIter.hasNext()) {
                    if (!this.rowIter.hasNext()) {
                        return null;
                    }
                    this.rowEntry = this.rowIter.next();
                    this.colIter = this.rowEntry.getValue().entrySet().iterator();
                }
                if (!this.colIter.hasNext()) {
                    return null;
                }
                this.colEntry = this.colIter.next();
                return new Node(this.rowEntry, this.colEntry);
            }

            @Override
            public boolean hasNext() {
                return this.next != null;
            }

            @Override
            public Entry<R, C, V> next() {
                Entry entry = this.next;
                this.next = this.getNext();
                return entry;
            }

            @Override
            public void remove() {
                this.colIter.remove();
            }
        };
    }

    public void replaceAll(TableFunction<R, C, V, V> tableFunction) {
        for (Entry<R, C, V> entry : this) {
            entry.setValue(tableFunction.compose(entry.getRow(), entry.getCol(), entry.getValue()));
        }
    }

    public V remove(R r, C c) {
        Map<C, V> map = this.rowMap.get(r);
        if (map == null) {
            return null;
        }
        return map.remove(c);
    }

    @Nullable
    public V replace(R r, C c, V v) {
        Map<C, V> map = this.getColMapIfExists(r);
        if (map == null) {
            return null;
        }
        if (map.get(c) != null || map.containsKey(c)) {
            return map.put(c, v);
        }
        return null;
    }

    @Nullable
    public boolean replace(R r, C c, V v, V v2) {
        Map<C, V> map = this.getColMapIfExists(r);
        if (map == null) {
            return false;
        }
        if (Objects.equals(map.get(c), v)) {
            map.put(c, v2);
            return true;
        }
        return false;
    }

    public V computeIfAbsent(R r, C c, BiFunction<R, C, V> biFunction) {
        return (V)this.getColMapForWrite(r).computeIfAbsent(c, object3 -> biFunction.apply(r, c));
    }

    public V computeIfPresent(R r, C c, TableFunction<R, C, V, V> tableFunction) {
        Map<C, Object> map = this.getColMapForWrite(r);
        Object object = map.computeIfPresent(c, (object3, object4) -> tableFunction.compose(r, c, object4));
        this.removeIfEmpty(r, map);
        return (V)object;
    }

    public V compute(R r, C c, TableFunction<R, C, V, V> tableFunction) {
        Map<C, Object> map = this.getColMapForWrite(r);
        Object object = map.compute(c, (object3, object4) -> tableFunction.compose(r, c, object4));
        this.removeIfEmpty(r, map);
        return (V)object;
    }

    public V merge(R r, C c, V v, TableFunction<R, C, V, V> tableFunction) {
        Map<C, Object> map = this.getColMapForWrite(r);
        Object object = map.merge(c, v, (object3, object4) -> tableFunction.compose(r, c, object4));
        this.removeIfEmpty(r, map);
        return (V)object;
    }

    public Map<C, V> row(final R r) {
        final HashMap hashMap = new HashMap(0);
        return new DelegatingMap<C, V>(){

            @Override
            public Map<C, V> delegate(boolean bl) {
                if (bl) {
                    return Table.this.rowMap.getOrDefault(r, hashMap);
                }
                return Table.this.getColMapForWrite(r);
            }

            @Override
            public V remove(Object object) {
                Map map = this.delegate(false);
                Object v = map.remove(object);
                Table.this.removeIfEmpty(r, map);
                return v;
            }
        };
    }

    private V getIfExists(R r, C c) {
        Map<C, V> map = this.getColMapIfExists(r);
        if (map == null) {
            return null;
        }
        return map.get(c);
    }

    private Map<C, V> getColMapIfExists(R r) {
        Map<C, V> map = this.rowMap.get(r);
        if (map != null && map.isEmpty()) {
            this.rowMap.remove(r);
            map = null;
        }
        return map;
    }

    private Map<C, V> getColMapForWrite(R r) {
        return this.rowMap.computeIfAbsent(r, this.colMapSupplier);
    }

    private void removeIfEmpty(R r, Map<C, V> map) {
        if (map.isEmpty()) {
            this.rowMap.remove(r);
        }
    }

    private class Node
    implements Entry<R, C, V> {
        private final Map.Entry<R, Map<C, V>> rowEntry;
        private final Map.Entry<C, V> colEntry;

        Node(Map.Entry<R, Map<C, V>> entry, Map.Entry<C, V> entry2) {
            this.rowEntry = entry;
            this.colEntry = entry2;
        }

        @Override
        public final R getRow() {
            return this.rowEntry.getKey();
        }

        @Override
        public final C getCol() {
            return this.colEntry.getKey();
        }

        @Override
        public final V getValue() {
            return this.colEntry.getValue();
        }

        @Override
        public final V setValue(V v) {
            return this.colEntry.setValue(v);
        }
    }

    public static interface Entry<R, C, V> {
        public R getRow();

        public C getCol();

        public V getValue();

        public V setValue(V var1);
    }

    public static interface TableConsumer<R, C, V> {
        public void accept(R var1, C var2, V var3);
    }

    public static interface TableFunction<R, C, V, RETURN> {
        public RETURN compose(R var1, C var2, V var3);
    }

    public static interface TablePredicate<R, C, V> {
        public boolean test(R var1, C var2, V var3);
    }
}

