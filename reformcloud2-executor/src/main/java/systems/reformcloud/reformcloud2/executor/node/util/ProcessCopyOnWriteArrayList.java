package systems.reformcloud.reformcloud2.executor.node.util;

import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class ProcessCopyOnWriteArrayList extends CopyOnWriteArrayList<ProcessInformation> {

    private final Object lock = new Object();

    @Override
    public boolean addAll(Collection<? extends ProcessInformation> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean add(ProcessInformation c) {
        this.stream()
                .filter(e -> e.getProcessDetail().getProcessUniqueID().equals(c.getProcessDetail().getProcessUniqueID()))
                .forEach(this::remove);
        return super.add(c);
    }

    @Override
    public void add(int index, ProcessInformation element) {
        this.add(element);
    }

    @Override
    public boolean addAll(int index, Collection<? extends ProcessInformation> c) {
        this.addAll(c);
        return true;
    }

    @Override
    public Stream<ProcessInformation> stream() {
        synchronized (lock) {
            return super.stream();
        }
    }

    @Override
    public Stream<ProcessInformation> parallelStream() {
        synchronized (lock) {
            return super.parallelStream();
        }
    }
}
