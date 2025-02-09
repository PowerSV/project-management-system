package ru.ballo.projects.unit;

import ru.ballo.projects.dto.member.CreateUpdateMemberDTO;
import ru.ballo.projects.dto.member.MemberDTO;
import ru.ballo.projects.mapping.MemberMapper;
import ru.ballo.projects.models.Member;
import ru.ballo.projects.models.statuses.MemberStatus;
import ru.ballo.projects.repos.JpaRepos.MemberJpaRepository;
import ru.ballo.projects.services.impl.MemberServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceImplTest {

    @Spy
    private MemberJpaRepository memberRepo;

    @Spy
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    public void createTest() {
        CreateUpdateMemberDTO newMember = new CreateUpdateMemberDTO();
        newMember.setFirstName("John");
        newMember.setLastName("Doe");

        Member member = new Member();
        member.setStatus(MemberStatus.ACTIVE);
        member.setFirstName(newMember.getFirstName());
        member.setLastName(newMember.getLastName());

        Member dbMember = new Member();
        dbMember.setStatus(MemberStatus.ACTIVE);
        dbMember.setFirstName(newMember.getFirstName());
        dbMember.setLastName(newMember.getLastName());
        dbMember.setId(1L);

        MemberDTO expectedMemberDTO = new MemberDTO();
        expectedMemberDTO.setId(dbMember.getId());
        expectedMemberDTO.setStatus(member.getStatus().toString());
        expectedMemberDTO.setDisplayName(member.getFirstName() + " " + member.getLastName());

        when(memberMapper.create(newMember)).thenReturn(member);
        when(memberRepo.save(member)).thenReturn(dbMember);
        when(memberMapper.map(dbMember)).thenReturn(expectedMemberDTO);

        // Actual
        MemberDTO createdMemberDTO = memberService.create(newMember);

        // Assert
        assertThat(createdMemberDTO).isEqualTo(expectedMemberDTO);
        assertThat(createdMemberDTO.getId()).isNotNull();
    }

    @Test
    void update_WhenMemberNotFound_ThrowsException() {
        // Arrange
        CreateUpdateMemberDTO updateMemberDTO = new CreateUpdateMemberDTO();
        updateMemberDTO.setId(1L);

        when(memberRepo.findById(updateMemberDTO.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> memberService.update(updateMemberDTO));

        verify(memberRepo, times(1)).findById(updateMemberDTO.getId());
        verify(memberRepo, never()).save(any(Member.class));
        verify(memberMapper, never()).map(any(Member.class));
    }

    @Test
    void update_WhenMemberStatusIsDeleted_ThrowsException() {
        // Arrange
        CreateUpdateMemberDTO updateMemberDTO = new CreateUpdateMemberDTO();
        updateMemberDTO.setId(1L);

        Member existingMember = new Member();
        existingMember.setId(updateMemberDTO.getId());
        existingMember.setStatus(MemberStatus.DELETED);

        when(memberRepo.findById(updateMemberDTO.getId())).thenReturn(Optional.of(existingMember));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> memberService.update(updateMemberDTO));

        verify(memberRepo, times(1)).findById(updateMemberDTO.getId());
        verify(memberRepo, never()).save(any(Member.class));
        verify(memberMapper, never()).map(any(Member.class));
    }

    @Test
    void update_WhenAllFieldsChanged_SuccessfullyUpdated() {
        // Arrange
        CreateUpdateMemberDTO updateDTO = new CreateUpdateMemberDTO();
        updateDTO.setId(1L);
        updateDTO.setFirstName("John");
        updateDTO.setLastName("Doe");
        updateDTO.setEmail("john@mail.com");
        updateDTO.setAccount("john123");

        Member existingMember = new Member();
        existingMember.setId(updateDTO.getId());
        existingMember.setStatus(MemberStatus.ACTIVE);
        existingMember.setFirstName("Ivan");
        existingMember.setLastName("Ivanov");
        existingMember.setMiddleName("Ivanovich");
        existingMember.setEmail("john@yandex.ru");
        existingMember.setAccount("jo1235678");

        Member updatedMember = new Member();
        updatedMember.setId(existingMember.getId());
        updatedMember.setStatus(MemberStatus.ACTIVE);
        updatedMember.setFirstName(updateDTO.getFirstName());
        updatedMember.setLastName(updateDTO.getLastName());
        updatedMember.setMiddleName(existingMember.getMiddleName());
        updatedMember.setEmail(updateDTO.getEmail());
        updatedMember.setAccount(updateDTO.getAccount());

        MemberDTO expectedMemberDTO = new MemberDTO();
        expectedMemberDTO.setId(updatedMember.getId());
        expectedMemberDTO.setStatus(updatedMember.getStatus().toString());
        expectedMemberDTO.setDisplayName(updatedMember.getFirstName()
                + " " + updatedMember.getLastName()
                + " " + updatedMember.getMiddleName());
        expectedMemberDTO.setEmail(updatedMember.getEmail());

        when(memberRepo.findById(updateDTO.getId())).thenReturn(Optional.of(existingMember));
        when(memberRepo.save(updatedMember)).thenReturn(updatedMember);
        when(memberMapper.map(updatedMember)).thenReturn(expectedMemberDTO);

        // Act
        MemberDTO updatedMemberDTO = memberService.update(updateDTO);

        // Assert
        assertThat(updatedMemberDTO).isEqualTo(expectedMemberDTO);
        assertThat(updatedMemberDTO.getDisplayName()).isEqualTo(expectedMemberDTO.getDisplayName());
        assertThat(updatedMemberDTO.getEmail()).isEqualTo(updateDTO.getEmail());
        assertThat(updatedMember.getAccount()).isEqualTo(updateDTO.getAccount());
        verify(memberRepo, times(1)).findById(updateDTO.getId());
        verify(memberRepo, times(1)).save(updatedMember);
        verify(memberMapper, times(1)).map(updatedMember);
    }

    @Test
    void update_WhenNoFieldsChanged_ReturnsUnchangedMemberDTO() {
        // Arrange
        CreateUpdateMemberDTO updateDTO = new CreateUpdateMemberDTO();
        updateDTO.setId(1L);

        Member existingMember = new Member();
        existingMember.setId(updateDTO.getId());
        existingMember.setStatus(MemberStatus.ACTIVE);
        existingMember.setFirstName("John");
        existingMember.setLastName("Doe");

        MemberDTO expectedMemberDTO = new MemberDTO();
        expectedMemberDTO.setId(existingMember.getId());
        expectedMemberDTO.setStatus(MemberStatus.ACTIVE.toString());
        expectedMemberDTO.setDisplayName(existingMember.getFirstName()
                + " " + existingMember.getLastName());

        when(memberRepo.findById(updateDTO.getId())).thenReturn(Optional.of(existingMember));
        when(memberRepo.save(existingMember)).thenReturn(existingMember);
        when(memberMapper.map(existingMember)).thenReturn(expectedMemberDTO);

        // Act
        MemberDTO updatedMemberDTO = memberService.update(updateDTO);

        // Assert
        assertThat(updatedMemberDTO).isEqualTo(expectedMemberDTO);
        verify(memberRepo, times(1)).findById(updateDTO.getId());
        verify(memberRepo, times(1)).save(any(Member.class));
        verify(memberMapper, times(1)).map(any(Member.class));
    }

    @Test
    void getAccount_WhenAccountIsNull_ReturnsNull() {
        // Arrange
        CreateUpdateMemberDTO dto = new CreateUpdateMemberDTO();
        dto.setEmail("john@mail.com");

        // Act
        String account = memberService.getAccount(dto);

        // Assert
        assertThat(account).isEqualTo("john");
    }

    @Test
    void getAccount_WhenAccountIsEmpty_ReturnsNull() {
        // Arrange
        CreateUpdateMemberDTO dto = new CreateUpdateMemberDTO();
        dto.setEmail("john@mail.com");
        dto.setAccount("john");

        // Act
        String account = memberService.getAccount(dto);

        // Assert
        assertThat(account).isEqualTo("john");
    }

    @Test
    void getAccount_WhenAccountIsNotBlank_ReturnsAccount() {
        // Arrange
        CreateUpdateMemberDTO dto = new CreateUpdateMemberDTO();
        dto.setEmail("john@mail.com");
        dto.setAccount("john123");

        // Act
        String account = memberService.getAccount(dto);

        // Assert
        assertThat(account).isEqualTo("john123");
    }

    @Test
    void getAccount_WhenEmailIsNull_ReturnsNull() {
        // Arrange
        CreateUpdateMemberDTO dto = new CreateUpdateMemberDTO();

        // Act
        String account = memberService.getAccount(dto);

        // Assert
        assertNull(account);
    }

    @Test
    void getAccount_WhenEmailIsEmpty_ReturnsNull() {
        // Arrange
        CreateUpdateMemberDTO dto = new CreateUpdateMemberDTO();
        dto.setEmail("");

        // Act
        String account = memberService.getAccount(dto);

        // Assert
        assertNull(account);
    }


    @Test
    void getAccount_WhenEmailIsValid_ReturnsAccountFromEmail() {
        // Arrange
        CreateUpdateMemberDTO dto = new CreateUpdateMemberDTO();
        dto.setEmail("john@mail.com");

        // Act
        String account = memberService.getAccount(dto);

        // Assert
        assertThat(account).isEqualTo("john");
    }

    @Test
    void deleteFromStorage_WhenMemberExists_SuccessfullyDeleted() {
        // Arrange
        Long memberId = 1L;
        Member deletedMember = new Member();
        deletedMember.setId(memberId);
        deletedMember.setStatus(MemberStatus.DELETED);
        deletedMember.setFirstName("John");
        deletedMember.setLastName("Doe");

        MemberDTO expected = new MemberDTO();
        expected.setId(memberId);
        expected.setDisplayName(deletedMember.getFirstName() + " " + deletedMember.getLastName());
        expected.setStatus(MemberStatus.DELETED.toString());

        when(memberRepo.findById(memberId)).thenReturn(Optional.of(deletedMember));
        when(memberMapper.map(deletedMember)).thenReturn(expected);

        // Act
        MemberDTO deletedMemberDTO = memberService.deleteFromStorage(memberId);

        // Assert
        assertThat(deletedMemberDTO.getId()).isEqualTo(memberId);
        verify(memberRepo, times(1)).findById(memberId);
        verify(memberRepo, times(1)).deleteMemberById(memberId);
        verify(memberMapper, times(1)).map(deletedMember);
    }

    @Test
    void deleteFromStorage_WhenMemberDoesNotExist_ThrowsException() {
        // Arrange
        Long memberId = 1L;

        when(memberRepo.findById(memberId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> memberService.deleteFromStorage(memberId));

        verify(memberRepo, times(1)).findById(memberId);
        verify(memberRepo, never()).deleteMemberById(anyLong());
        verify(memberMapper, never()).map(any(Member.class));
    }

    @Test
    void delete_WhenMemberExists_MemberDeletedSuccessfully() {
        // Arrange
        Long memberId = 1L;

        Member member = new Member();
        member.setId(memberId);
        member.setStatus(MemberStatus.ACTIVE);

        Member deletedMember = new Member();
        deletedMember.setId(memberId);
        deletedMember.setStatus(MemberStatus.DELETED);

        MemberDTO expectedDTO = new MemberDTO();
        expectedDTO.setId(memberId);
        expectedDTO.setStatus(MemberStatus.DELETED.toString());

        when(memberRepo.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepo.save(deletedMember)).thenReturn(deletedMember);
        when(memberMapper.map(deletedMember)).thenReturn(expectedDTO);

        // Act
        MemberDTO deletedMemberDTO = memberService.delete(memberId);

        // Assert
        assertThat(deletedMemberDTO.getStatus()).isEqualTo(MemberStatus.DELETED.toString());
        verify(memberRepo, times(1)).findById(memberId);
        verify(memberRepo, times(1)).save(deletedMember);
        verify(memberMapper, times(1)).map(deletedMember);
    }

    @Test
    void delete_WhenMemberDoesNotExist_ThrowsException() {
        // Arrange
        Long memberId = 1L;

        when(memberRepo.findById(memberId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> memberService.delete(memberId));
        verify(memberRepo, times(1)).findById(memberId);
        verify(memberRepo, never()).save(any(Member.class));
        verify(memberMapper, never()).map(any(Member.class));
    }

    @Test
    void search_WhenMembersFound_ReturnsMatchingMembers() {
        // Arrange
        String filter = "john";

        Member member1 = new Member();
        member1.setId(1L);
        member1.setFirstName("John");
        member1.setLastName("Doe");
        member1.setStatus(MemberStatus.ACTIVE);

        Member member2 = new Member();
        member2.setId(2L);
        member2.setFirstName("Jooooohn");
        member2.setLastName("Smith");
        member2.setStatus(MemberStatus.ACTIVE);

        MemberDTO expectedDTO1 = new MemberDTO();
        expectedDTO1.setId(member1.getId());
        expectedDTO1.setStatus(member1.getStatus().toString());
        expectedDTO1.setDisplayName(member1.getFirstName()
                + " " + member1.getLastName());

        MemberDTO expectedDTO2 = new MemberDTO();
        expectedDTO2.setId(member2.getId());
        expectedDTO2.setStatus(member2.getStatus().toString());
        expectedDTO2.setDisplayName(member2.getFirstName()
                + " " + member2.getLastName());


        List<Member> members = new ArrayList<>();
        members.add(member1);

        ArgumentCaptor<Specification<Member>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        when(memberRepo.findAll(specCaptor.capture())).thenReturn(members);
        when(memberMapper.map(member1)).thenReturn(expectedDTO1);

        // Act
        List<MemberDTO> matchingMembers = memberService.search(filter);

        // Assert
        assertThat(matchingMembers.size()).isEqualTo(1);
        verify(memberRepo, times(1)).findAll(specCaptor.capture());
        verify(memberMapper, times(1)).map(member1);
    }

    @Test
    void search_WhenFilterIsEmpty_ReturnsAllMembers() {
        // Arrange
        String filter = "";

        Member member1 = new Member();
        member1.setId(1L);
        member1.setFirstName("John");
        member1.setLastName("Doe");
        member1.setStatus(MemberStatus.ACTIVE);

        Member member2 = new Member();
        member2.setId(2L);
        member2.setFirstName("Jooooohn");
        member2.setLastName("Smith");
        member2.setStatus(MemberStatus.ACTIVE);

        MemberDTO expectedDTO1 = new MemberDTO();
        expectedDTO1.setId(member1.getId());
        expectedDTO1.setStatus(member1.getStatus().toString());
        expectedDTO1.setDisplayName(member1.getFirstName() + " " + member1.getLastName());

        MemberDTO expectedDTO2 = new MemberDTO();
        expectedDTO2.setId(member2.getId());
        expectedDTO2.setStatus(member2.getStatus().toString());
        expectedDTO2.setDisplayName(member2.getFirstName() + " " + member2.getLastName());

        List<Member> members = new ArrayList<>();
        members.add(member1);
        members.add(member2);

        ArgumentCaptor<Specification<Member>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        when(memberRepo.findAll(specCaptor.capture())).thenReturn(members);
        when(memberMapper.map(member1)).thenReturn(expectedDTO1);
        when(memberMapper.map(member2)).thenReturn(expectedDTO2);

        // Act
        List<MemberDTO> matchingMembers = memberService.search(filter);

        // Assert
        assertThat(matchingMembers.size()).isEqualTo(2);
        verify(memberRepo, times(1)).findAll(specCaptor.capture());
        verify(memberMapper, times(1)).map(member1);
        verify(memberMapper, times(1)).map(member2);
    }

    @Test
    void get_WhenMemberExists_ReturnsMemberDTO() {
        // Arrange
        Long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setStatus(MemberStatus.ACTIVE);
        MemberDTO expectedDTO = new MemberDTO();
        expectedDTO.setId(memberId);
        expectedDTO.setDisplayName("John Doe");
        expectedDTO.setStatus("ACTIVE");

        when(memberRepo.findById(memberId)).thenReturn(Optional.of(member));
        when(memberMapper.map(member)).thenReturn(expectedDTO);

        // Act
        MemberDTO resultDTO = memberService.get(memberId);

        // Assert
        assertThat(resultDTO).isEqualTo(expectedDTO);
    }

    @Test
    void get_WhenMemberDoesNotExist_ReturnsEmptyMemberDTO() {
        // Arrange
        Long memberId = 1L;
        when(memberRepo.findById(memberId)).thenReturn(Optional.empty());

        // Act
        MemberDTO resultDTO = memberService.get(memberId);

        // Assert
        assertThat(resultDTO).isEqualTo(new MemberDTO());
    }

    @Test
    void getAll_ReturnsAllMembers() {
        // Arrange
        Member member1 = new Member();
        member1.setId(1L);
        member1.setFirstName("John");
        member1.setLastName("Doe");
        member1.setStatus(MemberStatus.ACTIVE);

        Member member2 = new Member();
        member2.setId(2L);
        member2.setFirstName("Jane");
        member2.setLastName("Smith");
        member2.setStatus(MemberStatus.ACTIVE);

        MemberDTO expectedDTO1 = new MemberDTO();
        expectedDTO1.setId(1L);
        expectedDTO1.setDisplayName("John Doe");
        expectedDTO1.setStatus("ACTIVE");

        MemberDTO expectedDTO2 = new MemberDTO();
        expectedDTO2.setId(2L);
        expectedDTO2.setDisplayName("Jane Smith");
        expectedDTO2.setStatus("ACTIVE");

        List<Member> members = new ArrayList<>();
        members.add(member1);
        members.add(member2);

        List<MemberDTO> expectedDTOs = new ArrayList<>();
        expectedDTOs.add(expectedDTO1);
        expectedDTOs.add(expectedDTO2);

        when(memberRepo.findAll()).thenReturn(members);
        when(memberMapper.map(member1)).thenReturn(expectedDTO1);
        when(memberMapper.map(member2)).thenReturn(expectedDTO2);

        // Act
        List<MemberDTO> resultDTOs = memberService.getAll();

        // Assert
        assertThat(resultDTOs).isEqualTo(expectedDTOs);
    }
}
