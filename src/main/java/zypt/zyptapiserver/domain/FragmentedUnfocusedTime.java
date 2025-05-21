package zypt.zyptapiserver.domain;

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

    public FragmentedUnfocusedTime(Long id, LocalTime startAt, LocalTime endAt, UnFocusedType type) {
        this.startAt = startAt;
        this.endAt = endAt;
        this.type = type;
        this.unfocusedTime = calculateTotalUnFocusedTime();
    }

    private long calculateTotalUnFocusedTime() {
        return ChronoUnit.SECONDS.between(startAt, endAt);
    }

    public void setFocusTime(FocusTime focusTime) {
        this.focusTime = focusTime;
    }
}
