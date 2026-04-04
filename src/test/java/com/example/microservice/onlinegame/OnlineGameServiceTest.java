package com.example.microservice.onlinegame;

import com.example.microservice.onlinegame.model.Clan;
import com.example.microservice.onlinegame.model.Players;
import com.example.microservice.onlinegame.service.OnlineGameService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OnlineGameServiceTest {

    private final OnlineGameService service = new OnlineGameService();

    @Test
    void shouldCalculateGroupsForExample() {
        Players input = new Players(6, List.of(
            new Clan(4, 50),
            new Clan(2, 70),
            new Clan(6, 60),
            new Clan(1, 15),
            new Clan(5, 40),
            new Clan(3, 45),
            new Clan(1, 12),
            new Clan(4, 40)
        ));

        List<List<Clan>> result = service.calculate(input);

        assertThat(result).hasSize(5);

        assertThat(result.get(0)).containsExactly(
            new Clan(2, 70),
            new Clan(4, 50)
        );
        assertThat(result.get(1)).containsExactly(
            new Clan(6, 60)
        );
        assertThat(result.get(2)).containsExactly(
            new Clan(3, 45),
            new Clan(1, 15),
            new Clan(1, 12)
        );
        assertThat(result.get(3)).containsExactly(
            new Clan(4, 40)
        );
        assertThat(result.get(4)).containsExactly(
            new Clan(5, 40)
        );
    }

    @Test
    void shouldReturnEmptyListForNoClans() {
        Players input = new Players(6, List.of());

        List<List<Clan>> result = service.calculate(input);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldPlaceAllClansInOneGroupWhenTheyFit() {
        Players input = new Players(10, List.of(
            new Clan(3, 100),
            new Clan(2, 80),
            new Clan(4, 60)
        ));

        List<List<Clan>> result = service.calculate(input);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).containsExactly(
            new Clan(3, 100),
            new Clan(2, 80),
            new Clan(4, 60)
        );
    }

    @Test
    void shouldBreakTieByFewerPlayers() {
        Players input = new Players(5, List.of(
            new Clan(3, 50),
            new Clan(2, 50)
        ));

        List<List<Clan>> result = service.calculate(input);

        assertThat(result).hasSize(1);
        // Clan with 2 players comes first (same points, fewer players = higher priority)
        assertThat(result.get(0)).containsExactly(
            new Clan(2, 50),
            new Clan(3, 50)
        );
    }
}
