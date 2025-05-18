package zypt.zyptapiserver.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zypt.zyptapiserver.domain.enums.UnFocusedType;

import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FragmentedUnfocusedTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalTime startAt;
    private LocalTime endAt;

    @Enumerated(EnumType.STRING)
    private UnFocusedType type;
    private Long unfocusedTime;

    @ManyToOne
    @JoinColumn(name = "focus_id")
    private FocusTime focusTime;

}
