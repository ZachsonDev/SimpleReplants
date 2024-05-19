package de.jeff_media.replant.acf.commands.lib.expiringmap;

import de.jeff_media.replant.acf.commands.lib.expiringmap.EntryLoader;
import de.jeff_media.replant.acf.commands.lib.expiringmap.ExpirationListener;
import de.jeff_media.replant.acf.commands.lib.expiringmap.ExpirationPolicy;
import de.jeff_media.replant.acf.commands.lib.expiringmap.ExpiringEntryLoader;
import de.jeff_media.replant.acf.commands.lib.expiringmap.ExpiringValue;
import de.jeff_media.replant.acf.commands.lib.expiringmap.internal.Assert;
import de.jeff_media.replant.acf.commands.lib.expiringmap.internal.NamedThreadFactory;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ExpiringMap<K, V>
implements ConcurrentMap<K, V> {
    static volatile ScheduledExecutorService EXPIRER;
    static volatile ThreadPoolExecutor LISTENER_SERVICE;
    static ThreadFactory THREAD_FACTORY;
    List<ExpirationListener<K, V>> expirationListeners;
    List<ExpirationListener<K, V>> asyncExpirationListeners;
    private AtomicLong expirationNanos;
    private int maxSize;
    private final AtomicReference<ExpirationPolicy> expirationPolicy;
    private final EntryLoader<? super K, ? extends V> entryLoader;
    private final ExpiringEntryLoader<? super K, ? extends V> expiringEntryLoader;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = this.readWriteLock.readLock();
    private final Lock writeLock = this.readWriteLock.writeLock();
    private final EntryMap<K, V> entries;
    private final boolean variableExpiration;

    public static void setThreadFactory(ThreadFactory threadFactory) {
        THREAD_FACTORY = Assert.notNull(threadFactory, "threadFactory");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    private ExpiringMap(Builder<K, V> builder) {
        if (EXPIRER == null) {
            Class<ExpiringMap> clazz = ExpiringMap.class;
            // MONITORENTER : de.jeff_media.replant.acf.commands.lib.expiringmap.ExpiringMap.class
            if (EXPIRER == null) {
                EXPIRER = Executors.newSingleThreadScheduledExecutor(THREAD_FACTORY == null ? new NamedThreadFactory("ExpiringMap-Expirer") : THREAD_FACTORY);
            }
            // MONITOREXIT : clazz
        }
        if (LISTENER_SERVICE == null && ((Builder)builder).asyncExpirationListeners != null) {
            this.initListenerService();
        }
        this.variableExpiration = ((Builder)builder).variableExpiration;
        this.entries = (EntryMap)((Object)(this.variableExpiration ? new EntryTreeHashMap() : new EntryLinkedHashMap()));
        if (((Builder)builder).expirationListeners != null) {
            this.expirationListeners = new CopyOnWriteArrayList<ExpirationListener<K, V>>(((Builder)builder).expirationListeners);
        }
        if (((Builder)builder).asyncExpirationListeners != null) {
            this.asyncExpirationListeners = new CopyOnWriteArrayList<ExpirationListener<K, V>>(((Builder)builder).asyncExpirationListeners);
        }
        this.expirationPolicy = new AtomicReference<ExpirationPolicy>(((Builder)builder).expirationPolicy);
        this.expirationNanos = new AtomicLong(TimeUnit.NANOSECONDS.convert(((Builder)builder).duration, ((Builder)builder).timeUnit));
        this.maxSize = ((Builder)builder).maxSize;
        this.entryLoader = ((Builder)builder).entryLoader;
        this.expiringEntryLoader = ((Builder)builder).expiringEntryLoader;
    }

    public static Builder<Object, Object> builder() {
        return new Builder<Object, Object>();
    }

    public static <K, V> ExpiringMap<K, V> create() {
        return new ExpiringMap<Object, Object>(ExpiringMap.builder());
    }

    public synchronized void addExpirationListener(ExpirationListener<K, V> expirationListener) {
        Assert.notNull(expirationListener, "listener");
        if (this.expirationListeners == null) {
            this.expirationListeners = new CopyOnWriteArrayList<ExpirationListener<K, V>>();
        }
        this.expirationListeners.add(expirationListener);
    }

    public synchronized void addAsyncExpirationListener(ExpirationListener<K, V> expirationListener) {
        Assert.notNull(expirationListener, "listener");
        if (this.asyncExpirationListeners == null) {
            this.asyncExpirationListeners = new CopyOnWriteArrayList<ExpirationListener<K, V>>();
        }
        this.asyncExpirationListeners.add(expirationListener);
        if (LISTENER_SERVICE == null) {
            this.initListenerService();
        }
    }

    @Override
    public void clear() {
        this.writeLock.lock();
        try {
            for (ExpiringEntry expiringEntry : this.entries.values()) {
                expiringEntry.cancel();
            }
            this.entries.clear();
        }
        finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean containsKey(Object object) {
        this.readLock.lock();
        try {
            boolean bl = this.entries.containsKey(object);
            return bl;
        }
        finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object object) {
        this.readLock.lock();
        try {
            boolean bl = this.entries.containsValue(object);
            return bl;
        }
        finally {
            this.readLock.unlock();
        }
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new AbstractSet<Map.Entry<K, V>>(){

            @Override
            public void clear() {
                ExpiringMap.this.clear();
            }

            @Override
            public boolean contains(Object object) {
                if (!(object instanceof Map.Entry)) {
                    return false;
                }
                Map.Entry entry = (Map.Entry)object;
                return ExpiringMap.this.containsKey(entry.getKey());
            }

            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                return ExpiringMap.this.entries instanceof EntryLinkedHashMap ? (EntryLinkedHashMap)ExpiringMap.this.entries.new EntryLinkedHashMap.EntryIterator() : (EntryTreeHashMap)ExpiringMap.this.entries.new EntryTreeHashMap.EntryIterator();
            }

            @Override
            public boolean remove(Object object) {
                if (object instanceof Map.Entry) {
                    Map.Entry entry = (Map.Entry)object;
                    return ExpiringMap.this.remove(entry.getKey()) != null;
                }
                return false;
            }

            @Override
            public int size() {
                return ExpiringMap.this.size();
            }
        };
    }

    @Override
    public boolean equals(Object object) {
        this.readLock.lock();
        try {
            boolean bl = this.entries.equals(object);
            return bl;
        }
        finally {
            this.readLock.unlock();
        }
    }

    @Override
    public V get(Object object) {
        ExpiringEntry<K, V> expiringEntry = this.getEntry(object);
        if (expiringEntry == null) {
            return this.load(object);
        }
        if (ExpirationPolicy.ACCESSED.equals((Object)expiringEntry.expirationPolicy.get())) {
            this.resetEntry(expiringEntry, false);
        }
        return expiringEntry.getValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private V load(K k) {
        if (this.entryLoader == null && this.expiringEntryLoader == null) {
            return null;
        }
        this.writeLock.lock();
        try {
            ExpiringEntry<K, V> expiringEntry = this.getEntry(k);
            if (expiringEntry != null) {
                V v = expiringEntry.getValue();
                return v;
            }
            if (this.entryLoader != null) {
                V v = this.entryLoader.load(k);
                this.put(k, v);
                V v2 = v;
                return v2;
            }
            ExpiringValue<V> expiringValue = this.expiringEntryLoader.load(k);
            if (expiringValue == null) {
                this.put(k, null);
                V v = null;
                return v;
            }
            long l = expiringValue.getTimeUnit() == null ? this.expirationNanos.get() : expiringValue.getDuration();
            TimeUnit timeUnit = expiringValue.getTimeUnit() == null ? TimeUnit.NANOSECONDS : expiringValue.getTimeUnit();
            this.put(k, expiringValue.getValue(), expiringValue.getExpirationPolicy() == null ? this.expirationPolicy.get() : expiringValue.getExpirationPolicy(), l, timeUnit);
            V v = expiringValue.getValue();
            return v;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public long getExpiration() {
        return TimeUnit.NANOSECONDS.toMillis(this.expirationNanos.get());
    }

    public long getExpiration(K k) {
        Assert.notNull(k, "key");
        ExpiringEntry<K, V> expiringEntry = this.getEntry(k);
        Assert.element(expiringEntry, k);
        return TimeUnit.NANOSECONDS.toMillis(expiringEntry.expirationNanos.get());
    }

    public ExpirationPolicy getExpirationPolicy(K k) {
        Assert.notNull(k, "key");
        ExpiringEntry<K, V> expiringEntry = this.getEntry(k);
        Assert.element(expiringEntry, k);
        return expiringEntry.expirationPolicy.get();
    }

    public long getExpectedExpiration(K k) {
        Assert.notNull(k, "key");
        ExpiringEntry<K, V> expiringEntry = this.getEntry(k);
        Assert.element(expiringEntry, k);
        return TimeUnit.NANOSECONDS.toMillis(expiringEntry.expectedExpiration.get() - System.nanoTime());
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    @Override
    public int hashCode() {
        this.readLock.lock();
        try {
            int n = this.entries.hashCode();
            return n;
        }
        finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        this.readLock.lock();
        try {
            boolean bl = this.entries.isEmpty();
            return bl;
        }
        finally {
            this.readLock.unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        return new AbstractSet<K>(){

            @Override
            public void clear() {
                ExpiringMap.this.clear();
            }

            @Override
            public boolean contains(Object object) {
                return ExpiringMap.this.containsKey(object);
            }

            @Override
            public Iterator<K> iterator() {
                return ExpiringMap.this.entries instanceof EntryLinkedHashMap ? (EntryLinkedHashMap)ExpiringMap.this.entries.new EntryLinkedHashMap.KeyIterator() : (EntryTreeHashMap)ExpiringMap.this.entries.new EntryTreeHashMap.KeyIterator();
            }

            @Override
            public boolean remove(Object object) {
                return ExpiringMap.this.remove(object) != null;
            }

            @Override
            public int size() {
                return ExpiringMap.this.size();
            }
        };
    }

    @Override
    public V put(K k, V v) {
        Assert.notNull(k, "key");
        return this.putInternal(k, v, this.expirationPolicy.get(), this.expirationNanos.get());
    }

    public V put(K k, V v, ExpirationPolicy expirationPolicy) {
        return this.put(k, v, expirationPolicy, this.expirationNanos.get(), TimeUnit.NANOSECONDS);
    }

    public V put(K k, V v, long l, TimeUnit timeUnit) {
        return this.put(k, v, this.expirationPolicy.get(), l, timeUnit);
    }

    public V put(K k, V v, ExpirationPolicy expirationPolicy, long l, TimeUnit timeUnit) {
        Assert.notNull(k, "key");
        Assert.notNull(expirationPolicy, "expirationPolicy");
        Assert.notNull(timeUnit, "timeUnit");
        Assert.operation(this.variableExpiration, "Variable expiration is not enabled");
        return this.putInternal(k, v, expirationPolicy, TimeUnit.NANOSECONDS.convert(l, timeUnit));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        Assert.notNull(map, "map");
        long l = this.expirationNanos.get();
        ExpirationPolicy expirationPolicy = this.expirationPolicy.get();
        this.writeLock.lock();
        try {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                this.putInternal(entry.getKey(), entry.getValue(), expirationPolicy, l);
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V putIfAbsent(K k, V v) {
        Assert.notNull(k, "key");
        this.writeLock.lock();
        try {
            if (!this.entries.containsKey(k)) {
                V v2 = this.putInternal(k, v, this.expirationPolicy.get(), this.expirationNanos.get());
                return v2;
            }
            Object v3 = ((ExpiringEntry)this.entries.get(k)).getValue();
            return v3;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V remove(Object object) {
        Assert.notNull(object, "key");
        this.writeLock.lock();
        try {
            ExpiringEntry expiringEntry = (ExpiringEntry)this.entries.remove(object);
            if (expiringEntry == null) {
                V v = null;
                return v;
            }
            if (expiringEntry.cancel()) {
                this.scheduleEntry(this.entries.first());
            }
            Object v = expiringEntry.getValue();
            return v;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(Object object, Object object2) {
        Assert.notNull(object, "key");
        this.writeLock.lock();
        try {
            ExpiringEntry expiringEntry = (ExpiringEntry)this.entries.get(object);
            if (expiringEntry != null && expiringEntry.getValue().equals(object2)) {
                this.entries.remove(object);
                if (expiringEntry.cancel()) {
                    this.scheduleEntry(this.entries.first());
                }
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V replace(K k, V v) {
        Assert.notNull(k, "key");
        this.writeLock.lock();
        try {
            if (this.entries.containsKey(k)) {
                V v2 = this.putInternal(k, v, this.expirationPolicy.get(), this.expirationNanos.get());
                return v2;
            }
            V v3 = null;
            return v3;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean replace(K k, V v, V v2) {
        Assert.notNull(k, "key");
        this.writeLock.lock();
        try {
            ExpiringEntry expiringEntry = (ExpiringEntry)this.entries.get(k);
            if (expiringEntry != null && expiringEntry.getValue().equals(v)) {
                this.putInternal(k, v2, this.expirationPolicy.get(), this.expirationNanos.get());
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public void removeExpirationListener(ExpirationListener<K, V> expirationListener) {
        Assert.notNull(expirationListener, "listener");
        for (int i = 0; i < this.expirationListeners.size(); ++i) {
            if (!this.expirationListeners.get(i).equals(expirationListener)) continue;
            this.expirationListeners.remove(i);
            return;
        }
    }

    public void removeAsyncExpirationListener(ExpirationListener<K, V> expirationListener) {
        Assert.notNull(expirationListener, "listener");
        for (int i = 0; i < this.asyncExpirationListeners.size(); ++i) {
            if (!this.asyncExpirationListeners.get(i).equals(expirationListener)) continue;
            this.asyncExpirationListeners.remove(i);
            return;
        }
    }

    public void resetExpiration(K k) {
        Assert.notNull(k, "key");
        ExpiringEntry<K, V> expiringEntry = this.getEntry(k);
        if (expiringEntry != null) {
            this.resetEntry(expiringEntry, false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setExpiration(K k, long l, TimeUnit timeUnit) {
        Assert.notNull(k, "key");
        Assert.notNull(timeUnit, "timeUnit");
        Assert.operation(this.variableExpiration, "Variable expiration is not enabled");
        this.writeLock.lock();
        try {
            ExpiringEntry expiringEntry = (ExpiringEntry)this.entries.get(k);
            if (expiringEntry != null) {
                expiringEntry.expirationNanos.set(TimeUnit.NANOSECONDS.convert(l, timeUnit));
                this.resetEntry(expiringEntry, true);
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }

    public void setExpiration(long l, TimeUnit timeUnit) {
        Assert.notNull(timeUnit, "timeUnit");
        Assert.operation(this.variableExpiration, "Variable expiration is not enabled");
        this.expirationNanos.set(TimeUnit.NANOSECONDS.convert(l, timeUnit));
    }

    public void setExpirationPolicy(ExpirationPolicy expirationPolicy) {
        Assert.notNull(expirationPolicy, "expirationPolicy");
        this.expirationPolicy.set(expirationPolicy);
    }

    public void setExpirationPolicy(K k, ExpirationPolicy expirationPolicy) {
        Assert.notNull(k, "key");
        Assert.notNull(expirationPolicy, "expirationPolicy");
        Assert.operation(this.variableExpiration, "Variable expiration is not enabled");
        ExpiringEntry<K, V> expiringEntry = this.getEntry(k);
        if (expiringEntry != null) {
            expiringEntry.expirationPolicy.set(expirationPolicy);
        }
    }

    public void setMaxSize(int n) {
        Assert.operation(n > 0, "maxSize");
        this.maxSize = n;
    }

    @Override
    public int size() {
        this.readLock.lock();
        try {
            int n = this.entries.size();
            return n;
        }
        finally {
            this.readLock.unlock();
        }
    }

    public String toString() {
        this.readLock.lock();
        try {
            String string = this.entries.toString();
            return string;
        }
        finally {
            this.readLock.unlock();
        }
    }

    @Override
    public Collection<V> values() {
        return new AbstractCollection<V>(){

            @Override
            public void clear() {
                ExpiringMap.this.clear();
            }

            @Override
            public boolean contains(Object object) {
                return ExpiringMap.this.containsValue(object);
            }

            @Override
            public Iterator<V> iterator() {
                return ExpiringMap.this.entries instanceof EntryLinkedHashMap ? (EntryLinkedHashMap)ExpiringMap.this.entries.new EntryLinkedHashMap.ValueIterator() : (EntryTreeHashMap)ExpiringMap.this.entries.new EntryTreeHashMap.ValueIterator();
            }

            @Override
            public int size() {
                return ExpiringMap.this.size();
            }
        };
    }

    void notifyListeners(final ExpiringEntry<K, V> expiringEntry) {
        if (this.asyncExpirationListeners != null) {
            for (final ExpirationListener expirationListener : this.asyncExpirationListeners) {
                LISTENER_SERVICE.execute(new Runnable(){

                    @Override
                    public void run() {
                        try {
                            expirationListener.expired(expiringEntry.key, expiringEntry.getValue());
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                });
            }
        }
        if (this.expirationListeners != null) {
            for (final ExpirationListener expirationListener : this.expirationListeners) {
                try {
                    expirationListener.expired(expiringEntry.key, expiringEntry.getValue());
                }
                catch (Exception exception) {}
            }
        }
    }

    ExpiringEntry<K, V> getEntry(Object object) {
        this.readLock.lock();
        try {
            ExpiringEntry expiringEntry = (ExpiringEntry)this.entries.get(object);
            return expiringEntry;
        }
        finally {
            this.readLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    V putInternal(K k, V v, ExpirationPolicy expirationPolicy, long l) {
        this.writeLock.lock();
        try {
            ExpiringEntry<K, V> expiringEntry;
            ExpiringEntry<K, V> expiringEntry2 = (ExpiringEntry<K, V>)this.entries.get(k);
            ExpiringEntry<K, V> expiringEntry3 = null;
            if (expiringEntry2 == null) {
                expiringEntry2 = new ExpiringEntry<K, V>(k, v, this.variableExpiration ? new AtomicReference<ExpirationPolicy>(expirationPolicy) : this.expirationPolicy, this.variableExpiration ? new AtomicLong(l) : this.expirationNanos);
                if (this.entries.size() >= this.maxSize) {
                    expiringEntry = this.entries.first();
                    this.entries.remove(expiringEntry.key);
                    this.notifyListeners(expiringEntry);
                }
                this.entries.put(k, expiringEntry2);
                if (this.entries.size() == 1 || this.entries.first().equals(expiringEntry2)) {
                    this.scheduleEntry(expiringEntry2);
                }
            } else {
                expiringEntry3 = (ExpiringEntry<K, V>)expiringEntry2.getValue();
                if (!ExpirationPolicy.ACCESSED.equals((Object)expirationPolicy) && (expiringEntry3 == null && v == null || expiringEntry3 != null && ((Object)expiringEntry3).equals(v))) {
                    V v2 = v;
                    return v2;
                }
                expiringEntry2.setValue(v);
                this.resetEntry(expiringEntry2, false);
            }
            expiringEntry = expiringEntry3;
            return (V)expiringEntry;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void resetEntry(ExpiringEntry<K, V> expiringEntry, boolean bl) {
        this.writeLock.lock();
        try {
            boolean bl2 = expiringEntry.cancel();
            this.entries.reorder(expiringEntry);
            if (bl2 || bl) {
                this.scheduleEntry(this.entries.first());
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void scheduleEntry(ExpiringEntry<K, V> expiringEntry) {
        if (expiringEntry == null || expiringEntry.scheduled) {
            return;
        }
        Runnable runnable = null;
        ExpiringEntry<K, V> expiringEntry2 = expiringEntry;
        synchronized (expiringEntry2) {
            if (expiringEntry.scheduled) {
                return;
            }
            final WeakReference<ExpiringEntry<K, V>> weakReference = new WeakReference<ExpiringEntry<K, V>>(expiringEntry);
            runnable = new Runnable(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    ExpiringEntry expiringEntry = (ExpiringEntry)weakReference.get();
                    ExpiringMap.this.writeLock.lock();
                    try {
                        if (expiringEntry != null && expiringEntry.scheduled) {
                            ExpiringMap.this.entries.remove(expiringEntry.key);
                            ExpiringMap.this.notifyListeners(expiringEntry);
                        }
                        try {
                            Iterator iterator = ExpiringMap.this.entries.valuesIterator();
                            boolean bl = true;
                            while (iterator.hasNext() && bl) {
                                ExpiringEntry expiringEntry2 = iterator.next();
                                if (expiringEntry2.expectedExpiration.get() <= System.nanoTime()) {
                                    iterator.remove();
                                    ExpiringMap.this.notifyListeners(expiringEntry2);
                                    continue;
                                }
                                ExpiringMap.this.scheduleEntry(expiringEntry2);
                                bl = false;
                            }
                        }
                        catch (NoSuchElementException noSuchElementException) {
                            // empty catch block
                        }
                    }
                    finally {
                        ExpiringMap.this.writeLock.unlock();
                    }
                }
            };
            ScheduledFuture<?> scheduledFuture = EXPIRER.schedule(runnable, expiringEntry.expectedExpiration.get() - System.nanoTime(), TimeUnit.NANOSECONDS);
            expiringEntry.schedule(scheduledFuture);
        }
    }

    private static <K, V> Map.Entry<K, V> mapEntryFor(final ExpiringEntry<K, V> expiringEntry) {
        return new Map.Entry<K, V>(){

            @Override
            public K getKey() {
                return expiringEntry.key;
            }

            @Override
            public V getValue() {
                return expiringEntry.value;
            }

            @Override
            public V setValue(V v) {
                throw new UnsupportedOperationException();
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initListenerService() {
        Class<ExpiringMap> clazz = ExpiringMap.class;
        synchronized (ExpiringMap.class) {
            if (LISTENER_SERVICE == null) {
                LISTENER_SERVICE = (ThreadPoolExecutor)Executors.newCachedThreadPool(THREAD_FACTORY == null ? new NamedThreadFactory("ExpiringMap-Listener-%s") : THREAD_FACTORY);
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    static class ExpiringEntry<K, V>
    implements Comparable<ExpiringEntry<K, V>> {
        final AtomicLong expirationNanos;
        final AtomicLong expectedExpiration;
        final AtomicReference<ExpirationPolicy> expirationPolicy;
        final K key;
        volatile Future<?> entryFuture;
        V value;
        volatile boolean scheduled;

        ExpiringEntry(K k, V v, AtomicReference<ExpirationPolicy> atomicReference, AtomicLong atomicLong) {
            this.key = k;
            this.value = v;
            this.expirationPolicy = atomicReference;
            this.expirationNanos = atomicLong;
            this.expectedExpiration = new AtomicLong();
            this.resetExpiration();
        }

        @Override
        public int compareTo(ExpiringEntry<K, V> expiringEntry) {
            if (this.key.equals(expiringEntry.key)) {
                return 0;
            }
            return this.expectedExpiration.get() < expiringEntry.expectedExpiration.get() ? -1 : 1;
        }

        public int hashCode() {
            int n = 31;
            int n2 = 1;
            n2 = 31 * n2 + (this.key == null ? 0 : this.key.hashCode());
            n2 = 31 * n2 + (this.value == null ? 0 : this.value.hashCode());
            return n2;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            ExpiringEntry expiringEntry = (ExpiringEntry)object;
            if (!this.key.equals(expiringEntry.key)) {
                return false;
            }
            return !(this.value == null ? expiringEntry.value != null : !this.value.equals(expiringEntry.value));
        }

        public String toString() {
            return this.value.toString();
        }

        synchronized boolean cancel() {
            boolean bl = this.scheduled;
            if (this.entryFuture != null) {
                this.entryFuture.cancel(false);
            }
            this.entryFuture = null;
            this.scheduled = false;
            return bl;
        }

        synchronized V getValue() {
            return this.value;
        }

        void resetExpiration() {
            this.expectedExpiration.set(this.expirationNanos.get() + System.nanoTime());
        }

        synchronized void schedule(Future<?> future) {
            this.entryFuture = future;
            this.scheduled = true;
        }

        synchronized void setValue(V v) {
            this.value = v;
        }
    }

    private static class EntryTreeHashMap<K, V>
    extends HashMap<K, ExpiringEntry<K, V>>
    implements EntryMap<K, V> {
        private static final long serialVersionUID = 1L;
        SortedSet<ExpiringEntry<K, V>> sortedSet = new ConcurrentSkipListSet<ExpiringEntry<K, V>>();

        private EntryTreeHashMap() {
        }

        @Override
        public void clear() {
            super.clear();
            this.sortedSet.clear();
        }

        @Override
        public boolean containsValue(Object object) {
            for (ExpiringEntry expiringEntry : this.values()) {
                Object v = expiringEntry.value;
                if (v != object && (object == null || !object.equals(v))) continue;
                return true;
            }
            return false;
        }

        @Override
        public ExpiringEntry<K, V> first() {
            return this.sortedSet.isEmpty() ? null : this.sortedSet.first();
        }

        @Override
        public ExpiringEntry<K, V> put(K k, ExpiringEntry<K, V> expiringEntry) {
            this.sortedSet.add(expiringEntry);
            return super.put(k, expiringEntry);
        }

        @Override
        public ExpiringEntry<K, V> remove(Object object) {
            ExpiringEntry expiringEntry = (ExpiringEntry)super.remove(object);
            if (expiringEntry != null) {
                this.sortedSet.remove(expiringEntry);
            }
            return expiringEntry;
        }

        @Override
        public void reorder(ExpiringEntry<K, V> expiringEntry) {
            this.sortedSet.remove(expiringEntry);
            expiringEntry.resetExpiration();
            this.sortedSet.add(expiringEntry);
        }

        @Override
        public Iterator<ExpiringEntry<K, V>> valuesIterator() {
            return new ExpiringEntryIterator();
        }

        final class EntryIterator
        extends AbstractHashIterator
        implements Iterator<Map.Entry<K, V>> {
            EntryIterator() {
            }

            @Override
            public final Map.Entry<K, V> next() {
                return ExpiringMap.mapEntryFor(this.getNext());
            }
        }

        final class ValueIterator
        extends AbstractHashIterator
        implements Iterator<V> {
            ValueIterator() {
            }

            @Override
            public final V next() {
                return this.getNext().value;
            }
        }

        final class KeyIterator
        extends AbstractHashIterator
        implements Iterator<K> {
            KeyIterator() {
            }

            @Override
            public final K next() {
                return this.getNext().key;
            }
        }

        final class ExpiringEntryIterator
        extends AbstractHashIterator
        implements Iterator<ExpiringEntry<K, V>> {
            ExpiringEntryIterator() {
            }

            @Override
            public final ExpiringEntry<K, V> next() {
                return this.getNext();
            }
        }

        abstract class AbstractHashIterator {
            private final Iterator<ExpiringEntry<K, V>> iterator;
            protected ExpiringEntry<K, V> next;

            AbstractHashIterator() {
                this.iterator = EntryTreeHashMap.this.sortedSet.iterator();
            }

            public boolean hasNext() {
                return this.iterator.hasNext();
            }

            public ExpiringEntry<K, V> getNext() {
                this.next = this.iterator.next();
                return this.next;
            }

            public void remove() {
                EntryTreeHashMap.super.remove(this.next.key);
                this.iterator.remove();
            }
        }
    }

    private static class EntryLinkedHashMap<K, V>
    extends LinkedHashMap<K, ExpiringEntry<K, V>>
    implements EntryMap<K, V> {
        private static final long serialVersionUID = 1L;

        private EntryLinkedHashMap() {
        }

        @Override
        public boolean containsValue(Object object) {
            for (ExpiringEntry expiringEntry : this.values()) {
                Object v = expiringEntry.value;
                if (v != object && (object == null || !object.equals(v))) continue;
                return true;
            }
            return false;
        }

        @Override
        public ExpiringEntry<K, V> first() {
            return this.isEmpty() ? null : (ExpiringEntry)this.values().iterator().next();
        }

        @Override
        public void reorder(ExpiringEntry<K, V> expiringEntry) {
            this.remove(expiringEntry.key);
            expiringEntry.resetExpiration();
            this.put(expiringEntry.key, expiringEntry);
        }

        @Override
        public Iterator<ExpiringEntry<K, V>> valuesIterator() {
            return this.values().iterator();
        }

        public final class EntryIterator
        extends AbstractHashIterator
        implements Iterator<Map.Entry<K, V>> {
            @Override
            public final Map.Entry<K, V> next() {
                return ExpiringMap.mapEntryFor(this.getNext());
            }
        }

        final class ValueIterator
        extends AbstractHashIterator
        implements Iterator<V> {
            ValueIterator() {
            }

            @Override
            public final V next() {
                return this.getNext().value;
            }
        }

        final class KeyIterator
        extends AbstractHashIterator
        implements Iterator<K> {
            KeyIterator() {
            }

            @Override
            public final K next() {
                return this.getNext().key;
            }
        }

        abstract class AbstractHashIterator {
            private final Iterator<Map.Entry<K, ExpiringEntry<K, V>>> iterator;
            private ExpiringEntry<K, V> next;

            AbstractHashIterator() {
                this.iterator = EntryLinkedHashMap.this.entrySet().iterator();
            }

            public boolean hasNext() {
                return this.iterator.hasNext();
            }

            public ExpiringEntry<K, V> getNext() {
                this.next = this.iterator.next().getValue();
                return this.next;
            }

            public void remove() {
                this.iterator.remove();
            }
        }
    }

    private static interface EntryMap<K, V>
    extends Map<K, ExpiringEntry<K, V>> {
        public ExpiringEntry<K, V> first();

        public void reorder(ExpiringEntry<K, V> var1);

        public Iterator<ExpiringEntry<K, V>> valuesIterator();
    }

    public static final class Builder<K, V> {
        private ExpirationPolicy expirationPolicy = ExpirationPolicy.CREATED;
        private List<ExpirationListener<K, V>> expirationListeners;
        private List<ExpirationListener<K, V>> asyncExpirationListeners;
        private TimeUnit timeUnit = TimeUnit.SECONDS;
        private boolean variableExpiration;
        private long duration = 60L;
        private int maxSize = Integer.MAX_VALUE;
        private EntryLoader<K, V> entryLoader;
        private ExpiringEntryLoader<K, V> expiringEntryLoader;

        private Builder() {
        }

        public <K1 extends K, V1 extends V> ExpiringMap<K1, V1> build() {
            return new ExpiringMap(this);
        }

        public Builder<K, V> expiration(long l, TimeUnit timeUnit) {
            this.duration = l;
            this.timeUnit = Assert.notNull(timeUnit, "timeUnit");
            return this;
        }

        public Builder<K, V> maxSize(int n) {
            Assert.operation(n > 0, "maxSize");
            this.maxSize = n;
            return this;
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> entryLoader(EntryLoader<? super K1, ? super V1> entryLoader) {
            this.assertNoLoaderSet();
            this.entryLoader = Assert.notNull(entryLoader, "loader");
            return this;
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> expiringEntryLoader(ExpiringEntryLoader<? super K1, ? super V1> expiringEntryLoader) {
            this.assertNoLoaderSet();
            this.expiringEntryLoader = Assert.notNull(expiringEntryLoader, "loader");
            this.variableExpiration();
            return this;
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> expirationListener(ExpirationListener<? super K1, ? super V1> expirationListener) {
            Assert.notNull(expirationListener, "listener");
            if (this.expirationListeners == null) {
                this.expirationListeners = new ArrayList<ExpirationListener<K, V>>();
            }
            this.expirationListeners.add(expirationListener);
            return this;
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> expirationListeners(List<ExpirationListener<? super K1, ? super V1>> list) {
            Assert.notNull(list, "listeners");
            if (this.expirationListeners == null) {
                this.expirationListeners = new ArrayList<ExpirationListener<K, V>>(list.size());
            }
            for (ExpirationListener<K1, V1> expirationListener : list) {
                this.expirationListeners.add(expirationListener);
            }
            return this;
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> asyncExpirationListener(ExpirationListener<? super K1, ? super V1> expirationListener) {
            Assert.notNull(expirationListener, "listener");
            if (this.asyncExpirationListeners == null) {
                this.asyncExpirationListeners = new ArrayList<ExpirationListener<K, V>>();
            }
            this.asyncExpirationListeners.add(expirationListener);
            return this;
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> asyncExpirationListeners(List<ExpirationListener<? super K1, ? super V1>> list) {
            Assert.notNull(list, "listeners");
            if (this.asyncExpirationListeners == null) {
                this.asyncExpirationListeners = new ArrayList<ExpirationListener<K, V>>(list.size());
            }
            for (ExpirationListener<K1, V1> expirationListener : list) {
                this.asyncExpirationListeners.add(expirationListener);
            }
            return this;
        }

        public Builder<K, V> expirationPolicy(ExpirationPolicy expirationPolicy) {
            this.expirationPolicy = Assert.notNull(expirationPolicy, "expirationPolicy");
            return this;
        }

        public Builder<K, V> variableExpiration() {
            this.variableExpiration = true;
            return this;
        }

        private void assertNoLoaderSet() {
            Assert.state(this.entryLoader == null && this.expiringEntryLoader == null, "Either entryLoader or expiringEntryLoader may be set, not both", new Object[0]);
        }
    }
}

