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
        Member saved = memberRepository.saveAndFlush(newMember());

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
        memberRepository.saveAndFlush(newMember());

        // when
        Throwable thrown = catchThrowable(() ->
                memberRepository.saveAndFlush(newMember())
        );

        // then
        assertThat(thrown).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void delete_removes_member_and_findById_returns_empty() {
        // given
        Member member = newMember();
        member = memberRepository.saveAndFlush(member);

        // when
        memberRepository.delete(member);
        entityManager.flush();
        entityManager.clear();

        Optional<Member> found = memberRepository.findById(member.getId());

        // then
        assertThat(found).isEmpty();
    }

    private static Member newMember() {
        return new Member(null, "Steve", "1234", "abc@gmail.com");
    }
}
