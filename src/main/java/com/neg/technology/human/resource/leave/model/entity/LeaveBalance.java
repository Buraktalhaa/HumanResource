package com.neg.technology.human.resource.leave.model.entity;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.utility.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "leave_balance",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"employee_id", "leave_type_id", "effective_date"}
        )
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveBalance extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // Lazy patlamasın diye
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;


    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate; // Yılın başlangıcını temsil edecek

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "used_days")
    @Builder.Default
    private Integer usedDays = 0;

    /**
     * Kullanılabilir bakiye hesaplama helper metodu
     */
    public BigDecimal getAvailableBalance() {
        return amount.subtract(BigDecimal.valueOf(usedDays));
    }

    /**
     * Kullanılabilir bakiye varsa düşme işlemi
     */
    public void deduct(BigDecimal days) {
        if (getAvailableBalance().compareTo(days) < 0) {
            throw new IllegalArgumentException("Insufficient leave balance.");
        }
        this.usedDays += days.intValue();
    }

    /**
     * Bakiye ekleme helper
     */
    public void add(BigDecimal days) {
        this.amount = this.amount.add(days);
    }
}
