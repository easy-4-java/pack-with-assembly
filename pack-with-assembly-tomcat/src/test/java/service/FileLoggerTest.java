package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("FileLogger Tests")
class FileLoggerTest {

    @Test
    @DisplayName("should create FileLogger instance")
    void shouldCreateInstance() {
        FileLogger logger = new FileLogger();
        assertThat(logger).isNotNull();
    }

    @Test
    @DisplayName("should log 100 lines without error")
    void shouldLogWithoutError() {
        FileLogger logger = new FileLogger();
        assertThatCode(logger::logInfo2file).doesNotThrowAnyException();
    }
}
