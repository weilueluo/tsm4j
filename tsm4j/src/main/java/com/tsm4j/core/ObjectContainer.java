package com.tsm4j.core;

import java.util.Objects;

/*
* This class is to give the wrapped object a unique hashcode
* */
class ObjectContainer<T> {

    private final T obj;

    ObjectContainer(T obj) {
        this.obj = obj;
    }

    T get() {
        return this.obj;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hashCode(this.obj);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && getClass().equals(obj.getClass()) && hashCode() == obj.hashCode();
    }
}
