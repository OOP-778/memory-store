import com.oop.memorystore.implementation.memory.MemoryStore;
import com.oop.memorystore.implementation.query.Query;
import com.oop.memorystore.implementation.query.QueryOperator;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class Test {
    public static void main(String[] args) {
        final MemoryStore<TestObject> objects = new MemoryStore<>();
        objects.index("chunk", TestObject::getChunk);
        objects.index("value", TestObject::getValue);

        for (int i = 0; i < 40000; i++) {
            objects.add(new TestObject(
                ThreadLocalRandom.current().nextInt(0, 40),
                ThreadLocalRandom.current().nextInt(0, 4)
            ));
        }

        measure("get", () -> {
            System.out.println("get: " + objects.get(Query.where("chunk", 1).and("value", 2)).size());
        });

        measure("query", () -> {
            System.out.println("query: " + objects.createQuery()
                                                  .filter("chunk", QueryOperator.FIRST, 1, 2, 2, 5, 6)
                                                  .filter("value", 2)
                                                  .collect(new HashSet<>())
                                                  .size());
        });
    }

    protected static void measure(String name, Runnable task) {
        long start = System.currentTimeMillis();
        task.run();
        System.out.println(String.format("%s task took %s ms", name, (System.currentTimeMillis() - start)));
    }
}
