package tr.com.orioninc.laborant.app.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tr.com.orioninc.laborant.app.model.Lab;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Log4j2
public class LabRepositoryTest {

    @Autowired
    private LabRepository underTest;

    @Test
    void itShouldCheckIfLabExists() {
        // given
        String labName = "testLab";
        Lab lab = new Lab(
                labName, "testUser", "testPassword", "testHost", 22);
        log.info(underTest.save(lab));
        // when
        Lab foundLab = underTest.findByLabName(labName);

        // then
        assertThat(foundLab).isNotNull();
        log.info("Lab found: {}", foundLab);
    }

    @Test
    void itShouldCheckIfLabNotExists() {
        // given
        String labName = "testAsdLab";
        Lab lab = new Lab(
                labName, "testUser", "testPassword", "testHost", 22);
        underTest.save(lab);
        // when
        Lab foundLab = underTest.findByLabName("testLab");

        // then
        assertThat(foundLab).isNull();
        log.info("Lab not found");
    }
    @Test
    void itShouldCheckIfHostAndUserNameExists() {
        // given
        String labName = "testLab";
        Lab lab = new Lab(
                labName, "testUser", "testPassword", "testHost", 22);
        underTest.save(lab);
        // when
        Lab foundLab = underTest.findByUserNameAndHost(lab.getUserName(), lab.getHost());

        // then
        assertThat(foundLab).isNotNull();
        log.info("Lab found: {}", foundLab);
    }
    @Test
    void itShouldCheckIfHostAndUserNameNotExists() {
        // given
        String labName = "testLab";
        Lab lab = new Lab(
                labName, "testUser", "testPassword", "testHost", 22);
        underTest.save(lab);
        // when
        Lab foundLab = underTest.findByUserNameAndHost("testUser", "notExists");

        // then
        assertThat(foundLab).isNull();
        log.info("Lab not found");
    }
}