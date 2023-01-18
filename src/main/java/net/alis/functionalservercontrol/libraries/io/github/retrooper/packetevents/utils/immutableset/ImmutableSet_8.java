 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.immutableset;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableSet;

class ImmutableSet_8<T> extends ImmutableSetAbstract<T>
{
    private ImmutableSet<T> immutableSet;
    
    public ImmutableSet_8() {
        this.immutableSet = (ImmutableSet<T>)ImmutableSet.builder().build();
    }
    
    public ImmutableSet_8(final List<T> data) {
        this.immutableSet = (ImmutableSet<T>)ImmutableSet.builder().addAll(data).build();
    }
    
    @SafeVarargs
    public ImmutableSet_8(final T... data) {
        final ImmutableSet.Builder<T> builder = (ImmutableSet.Builder<T>)ImmutableSet.builder();
        for (final T value : data) {
            builder.add(value);
        }
        this.immutableSet = (ImmutableSet<T>)builder.build();
    }
    
    @Override
    public boolean contains(final T element) {
        return this.immutableSet.contains((Object)element);
    }
    
    @Override
    public void add(final T element) {
        final List<T> elements = new ArrayList<T>((Collection<? extends T>)this.immutableSet);
        this.immutableSet = (ImmutableSet<T>)ImmutableSet.builder().addAll((Iterable)elements).add((Object)element).build();
    }
    
    @SafeVarargs
    @Override
    public final void addAll(final T... elements) {
        final List<T> localElements = new ArrayList<T>((Collection<? extends T>)this.immutableSet);
        this.immutableSet = (ImmutableSet<T>)ImmutableSet.builder().addAll((Iterable)localElements).addAll((Iterable)Arrays.asList(elements)).build();
    }
}
