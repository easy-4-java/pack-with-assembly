package main;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("WrapperMainClassForWindows Tests")
class WrapperMainClassForWindowsTest {

    @Test
    @DisplayName("should execute main without error")
    void shouldExecuteMain() {
        assertThatCode(() -> WrapperMainClassForWindows.main(new String[]{})).doesNotThrowAnyException();
    }
}
