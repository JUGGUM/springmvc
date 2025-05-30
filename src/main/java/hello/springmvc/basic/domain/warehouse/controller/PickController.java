package hello.springmvc.basic.domain.warehouse.controller;

import hello.springmvc.basic.domain.warehouse.dto.PickItem;
import hello.springmvc.basic.domain.warehouse.service.PickOptimizeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class PickController {

    private final PickOptimizeService pickOptimizeService;

    public PickController(PickOptimizeService pickOptimizeService) {
        this.pickOptimizeService = pickOptimizeService;
    }

    @GetMapping("/api/pick/sorted")
    public List<PickItem> getSortedPickItems() {
        List<PickItem> list = Arrays.asList(
                new PickItem(1L, "3000", "A-AA-A01-01"),
                new PickItem(2L, "1000", "A-AA-A02-01"),
                new PickItem(3L, "2000", "A-AA-A01-02"),
                new PickItem(4L, "4000", "A-AA-A01-01"),
                new PickItem(5L, "1000", "A-AA-A01-01")
        );

        return pickOptimizeService.optimizePickOrder(list);
    }
}
