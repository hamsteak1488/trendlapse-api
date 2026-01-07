package io.github.hamsteak.trendlapse.member.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@DataJpaTest
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    void save_persists_member_and_findById_returns_it() {
        // given
        Member saved = memberRepository.saveAndFlush(new Member(null, "Steve", "1234", "abc@gmail.com"));

        entityManager.clear();

        // when
        Optional<Member> found = memberRepository.findById(saved.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    void save_throws_DataIntegrityViolationException_when_username_duplicated() {
        // given
        memberRepository.saveAndFlush(new Member(null, "Steve", "1234", "abc@gmail.com"));

        // when
        Throwable thrown = catchThrowable(() -> memberRepository.save(new Member(null, "Steve", "5678", "def@gmail.com")));

        // then
        assertThat(thrown).isInstanceOf(DataIntegrityViolationException.class);
    }
}
