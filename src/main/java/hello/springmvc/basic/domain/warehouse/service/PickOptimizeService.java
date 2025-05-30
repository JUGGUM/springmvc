package hello.springmvc.basic.domain.warehouse.service;

import hello.springmvc.basic.domain.warehouse.dto.PickItem;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PickOptimizeService {

    public List<PickItem> optimizePickOrder(List<PickItem> items) {
        List<String> pickOrdPriority = Arrays.asList("1", "2", "3", "4"); // 온도대 정렬 우선순위

        return items.stream()
                .sorted(Comparator
                        .comparing((PickItem item) ->
                                pickOrdPriority.indexOf(item.getPickListOrd().substring(0, 1))
                        )
                        .thenComparing(PickItem::getLocationCode)
                )
                .collect(Collectors.toList());
    }
}
