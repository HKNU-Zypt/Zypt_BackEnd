package zypt.zyptapiserver.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@MappedSuperclass
public class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false) // 업데이트 불가능하게 만듦
    private LocalDateTime createAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

}
