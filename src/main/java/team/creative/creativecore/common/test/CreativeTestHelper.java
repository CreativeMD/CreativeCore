package team.creative.creativecore.common.test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;

public class CreativeTestHelper {
    
    private int checks;
    private Object2LongMap<String> timings = new Object2LongArrayMap<>();
    
    public <T> T trackTime(String time, Supplier<T> supplier) {
        long start = System.nanoTime();
        T result = supplier.get();
        long duration = System.nanoTime() - start;
        timings.computeLong(time, (key, present) -> {
            if (present == null)
                return duration;
            return present + duration;
        });
        return result;
    }
    
    public void incrementCount() {
        checks++;
    }
    
    public void assertTrue(boolean value, String failMessage, Object... args) throws CreativeTestException {
        checks++;
        if (!value)
            throw new CreativeTestException(String.format(failMessage, args));
    }
    
    public void assertFalse(boolean value, String failMessage, Object... args) throws CreativeTestException {
        checks++;
        if (value)
            throw new CreativeTestException(String.format(failMessage, args));
    }
    
    public List<String> results() {
        List<String> results = new ArrayList<>();
        if (!timings.isEmpty()) {
            String text = "timings: ";
            for (Entry<String> entry : timings.object2LongEntrySet()) {
                long miliseconds = entry.getLongValue() / 1000000;
                if (miliseconds > 0)
                    text += entry.getKey() + ": " + miliseconds + "ms ";
                else
                    text += entry.getKey() + ": " + entry.getLongValue() + "ns ";
            }
            results.add(text);
        }
        results.add("checks: " + checks);
        return results;
    }
}
