package zypt.zyptapiserver.domain;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zypt.zyptapiserver.domain.enums.UnFocusedType;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor
public class FragmentedUnfocusedTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalTime startAt;
    private LocalTime endAt;

    @Enumerated(EnumType.STRING)
    private UnFocusedType type;
    private Long unfocusedTime;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "focus_id")
    private FocusTime focusTime;

    @QueryProjection
    public FragmentedUnfocusedTime(Long id, LocalTime startAt, LocalTime endAt, UnFocusedType type, Long unfocusedTime, FocusTime focusTime) {
        this.id = id;
        this.startAt = startAt;
        this.endAt = endAt;
        this.type = type;
        this.unfocusedTime = unfocusedTime;
        this.focusTime = focusTime;
    }


    private long calculateTotalUnFocusedTime() {
        return ChronoUnit.SECONDS.between(startAt, endAt);
    }

    public void setFocusTime(FocusTime focusTime) {
        this.focusTime = focusTime;
    }
}
