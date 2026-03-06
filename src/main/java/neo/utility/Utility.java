package neo.utility;

import java.util.ArrayList;
import java.util.List;

public class Utility {

    public static <T> List<List<T>> splitList(List<T> list, int partitionSize) {
        List<List<T>> partitions = new ArrayList<>();

        for (int i = 0; i < list.size(); i += partitionSize) {
            partitions.add(list.subList(i, Math.min(i + partitionSize, list.size())));
        }

        return partitions;
    }

}
