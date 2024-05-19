package de.jeff_media.replant.jefflib.data.tuples;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class Pair<A, B> {
    @Nullable
    A first;
    @Nullable
    B second;

    public Pair(@Nullable A a, @Nullable B b) {
        this.first = a;
        this.second = b;
    }

    public static <A, B> Pair<A, B> of(@Nullable A a, @Nullable B b) {
        return new Pair<A, B>(a, b);
    }

    @Nullable
    public A getFirst() {
        return this.first;
    }

    public void setFirst(@Nullable A a) {
        this.first = a;
    }

    @Nullable
    public B getSecond() {
        return this.second;
    }

    public void setSecond(@Nullable B b) {
        this.second = b;
    }

    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Pair pair = (Pair)object;
        return Objects.equals(this.first, pair.first) && Objects.equals(this.second, pair.second);
    }

    public String toString() {
        return "Pair{first=" + this.first + ", second=" + this.second + '}';
    }
}

