package com.task.rvt.mt.guice;

import java.lang.reflect.Type;

import com.google.inject.Binding;
import com.google.inject.Injector;

public class Scanner<T> {
    public interface Visitor<V> {
        void visit(V thing);
    }

    private final Injector injector;
    private final Class<T> scanFor;

    public Scanner(Injector injector, Class<T> scanFor) {
        this.injector = injector;
        this.scanFor = scanFor;
    }

    public void accept(Visitor<T> visitor) {
        accept(injector, visitor);
    }

    private void accept(Injector inj, Visitor visitor) {
        if (inj == null)
            return;

        accept(inj.getParent(), visitor);

        for (final Binding<?> binding : inj.getBindings().values()) {
            final Type type = binding.getKey().getTypeLiteral().getType();
            if (type instanceof Class && scanFor.isAssignableFrom((Class) type)) {
                visitor.visit(binding.getProvider().get());
            }
        }
    }
}