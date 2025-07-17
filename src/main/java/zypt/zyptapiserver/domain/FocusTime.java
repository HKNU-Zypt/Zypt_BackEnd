package zypt.zyptapiserver.domain;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class FocusTime {
    // wrapper 타입으로 id 설정, DB에서 자동으로 값을 채워주기에 기본타입은 0으로 자동 초기화되기에 혼란을 유발할 수 있음
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalTime startAt;
    private LocalTime endAt;
    private LocalDate createDate;
    private Long focusTime;
    private Long totalTime;

    @OneToMany(mappedBy = "focusTime", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FragmentedUnfocusedTime> unfocusedTimes = new ArrayList<>();

    @Builder
    @QueryProjection
    public FocusTime(Member member, LocalTime startAt, LocalTime endAt, LocalDate date, Long unFocusTime) {
        this.member = member;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createDate = date;
        this.totalTime = calculateTotalTime();
        this.focusTime = totalTime - unFocusTime;
    }

    public FocusTime(Long id, Member member, LocalTime startAt, LocalTime endAt, LocalDate date) {
        this.id = id;
        this.member = member;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createDate = date;
        this.totalTime = calculateTotalTime();
    }



    // 시작 시간과 끝시간의 차를 구해 총 서비스 이용시간을 구한다.
    private long calculateTotalTime() {
        return ChronoUnit.SECONDS.between(startAt, endAt);
    }

    // 집중하지 않은 시간을 계산해 반환
    public long calculateUnfocusedDuration() {
        if (focusTime == null) {
            throw new NullPointerException("focusTime is null");
        }
        return totalTime - focusTime;
    }

    // 집중시간을 초기화
    public void initFocusedTime(long unFocusedTime) {
        if (this.focusTime != null) {
            throw new IllegalStateException("focusTime has already been initialized");
        }
        this.focusTime = this.totalTime - unFocusedTime;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    // 연관관계 편의 메서드
    public void addFragmentedUnfocusedTime(FragmentedUnfocusedTime unfocusedTime) {
        unfocusedTimes.add(unfocusedTime);
        unfocusedTime.setFocusTime(this);
    }
}
