package vp.botv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vp.botv.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByClientTelegramId(Long clientTelegramId);

}
