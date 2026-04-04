package com.example.microservice.atmservice;

import com.example.microservice.atmservice.model.ATM;
import com.example.microservice.atmservice.model.RequestType;
import com.example.microservice.atmservice.model.Task;
import com.example.microservice.atmservice.service.ATMService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ATMServiceTest {

    private final ATMService service = new ATMService();

    @Test
    void shouldCalculateOrderForExample1() {
        List<Task> input = List.of(
            new Task(4, RequestType.STANDARD, 1),
            new Task(1, RequestType.STANDARD, 1),
            new Task(2, RequestType.STANDARD, 1),
            new Task(3, RequestType.PRIORITY, 2),
            new Task(3, RequestType.STANDARD, 1),
            new Task(2, RequestType.SIGNAL_LOW, 1),
            new Task(5, RequestType.STANDARD, 2),
            new Task(5, RequestType.FAILURE_RESTART, 1)
        );

        List<ATM> result = service.calculateOrder(input);

        assertThat(result).containsExactly(
            new ATM(1, 1),
            new ATM(2, 1),
            new ATM(3, 2),
            new ATM(3, 1),
            new ATM(4, 1),
            new ATM(5, 1),
            new ATM(5, 2)
        );
    }

    @Test
    void shouldCalculateOrderForExample2() {
        List<Task> input = List.of(
            new Task(1, RequestType.STANDARD, 2),
            new Task(1, RequestType.STANDARD, 1),
            new Task(2, RequestType.PRIORITY, 3),
            new Task(3, RequestType.STANDARD, 4),
            new Task(4, RequestType.STANDARD, 5),
            new Task(5, RequestType.PRIORITY, 2),
            new Task(5, RequestType.STANDARD, 1),
            new Task(3, RequestType.SIGNAL_LOW, 2),
            new Task(2, RequestType.SIGNAL_LOW, 1),
            new Task(3, RequestType.FAILURE_RESTART, 1)
        );

        List<ATM> result = service.calculateOrder(input);

        assertThat(result).containsExactly(
            new ATM(1, 2),
            new ATM(1, 1),
            new ATM(2, 3),
            new ATM(2, 1),
            new ATM(3, 1),
            new ATM(3, 2),
            new ATM(3, 4),
            new ATM(4, 5),
            new ATM(5, 2),
            new ATM(5, 1)
        );
    }

    @Test
    void shouldReturnEmptyListForEmptyInput() {
        List<ATM> result = service.calculateOrder(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeduplicateKeepingHighestPriority() {
        List<Task> input = List.of(
            new Task(1, RequestType.STANDARD, 1),
            new Task(1, RequestType.SIGNAL_LOW, 1),
            new Task(1, RequestType.PRIORITY, 1),
            new Task(1, RequestType.FAILURE_RESTART, 1)
        );

        List<ATM> result = service.calculateOrder(input);

        assertThat(result).containsExactly(new ATM(1, 1));
    }
}
