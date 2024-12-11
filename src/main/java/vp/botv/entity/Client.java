package vp.botv.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_telegram_id")
    private Long clientTelegramId;

    @Setter
    @Column(name = "total_paid")
    private Long totalPaid;

    @Setter
    @Column(name = "total_purchases")
    private Long totalPurchases;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Setter
    @Column(name = "got_free_trial")
    private Boolean gotFreeTrial;

    @OneToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY, mappedBy = "client")
    private List<Purchase> purchase;
}
