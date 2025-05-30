package hello.springmvc.basic.domain.warehouse.dto;

import lombok.Data;

@Data
public class PickItem {
    private Long id;
    private String pickListOrd;     // 예: 1000, 2000, ...
    private String locationCode;    // 예: A-AA-A10-01

    // 생성자, getter, setter 생략
}
