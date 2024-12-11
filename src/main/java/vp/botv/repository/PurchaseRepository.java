package vp.botv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vp.botv.entity.Purchase;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findAllByExpTimeIsBeforeAndIsClientWarnedFalse(LocalDateTime dateTime);

}
