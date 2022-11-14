package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.Interaction;

import java.util.Optional;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction,String> {

    Optional<Interaction> findResponseByRequest(String request);
}
