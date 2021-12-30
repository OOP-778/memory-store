import com.oop.memorystore.api.Store;
import com.oop.memorystore.implementation.index.IndexDefinition;
import com.oop.memorystore.implementation.memory.MemoryStore;
import com.oop.memorystore.implementation.query.Query;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Test {
    public static void main(String[] args) {
        final MemoryStore<TestObject> objects = new MemoryStore<>();
        final Store<TestObject> synchronizedStore = objects.synchronizedStore();
        synchronizedStore.index("value", IndexDefinition.withKeyMapping(TestObject::getValue));

        final ExecutorService executorService = Executors.newCachedThreadPool();

        synchronizedStore.add(new TestObject(10, 10));

        final AtomicReference<Boolean> run = new AtomicReference<>(false);

        for (int i = 0; i < 70; i++) {
            executorService.execute(() -> {
                while (!run.get()) {}


                final Optional<TestObject> value = synchronizedStore.findFirst(Query.where("value", 10));
                System.out.println(String.format("[%s %s]: %s", Thread.currentThread().getName(), System.currentTimeMillis(),
                    value.isPresent()));
            });
        }

        for (int i = 0; i < 70; i++) {
            executorService.execute(() -> {
                while (!run.get()) {}

                synchronizedStore.add(new TestObject(20,20));
            });
        }

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        run.set(true);

        try {
            executorService.shutdown();
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
