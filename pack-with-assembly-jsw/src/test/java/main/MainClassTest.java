package main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("MainClass Tests")
class MainClassTest {

    @Test
    @DisplayName("should execute main without error")
    void shouldExecuteMain() {
        assertThatCode(() -> MainClass.main(new String[]{})).doesNotThrowAnyException();
    }
}
