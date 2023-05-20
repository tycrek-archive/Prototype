package dev.jmoore.cache;

import dev.jmoore.Protocore;
import lombok.Getter;

@Getter
public abstract class Store {
    private final Protocore core;

    public Store(Protocore core) {
        this.core = core;
        clear();
    }

    public abstract void clear();
}
