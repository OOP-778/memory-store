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

        final TestObject testObject = new TestObject(10, 10);

        synchronizedStore.add(testObject);

        synchronizedStore.printDetails(testObject);
    }
}
