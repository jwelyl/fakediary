package com.a101.fakediary.member.service;

import com.a101.fakediary.member.dto.*;
import com.a101.fakediary.member.entity.Member;
import com.a101.fakediary.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    //회원가입
    @Transactional
    public Member signUpMember(MemberSaveRequestDto memberSaveRequestDto) {
        if(memberSaveRequestDto.getEmail() == null){
            throw new IllegalArgumentException("Email를 입력해 주세요.");
        }
        if(memberSaveRequestDto.getPassword() == null){
            throw new IllegalArgumentException("Password를 입력해 주세요.");
        }

        // 닉네임 중복 체크
        if (memberRepository.existsByNickname(memberSaveRequestDto.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(memberSaveRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Member member = memberSaveRequestDto.toEntity();
        String encodePassword = PasswordEncoder.sha256(member.getPassword()); // 시큐리티삭제해서 일단 Bcrypt대신 sha암호화
        member.setPassword(encodePassword);

        return memberRepository.save(member);
    }

    //카카오 소셜 회원가입
    @Transactional
    public Member kakaoSignUpMember(MemberKakaoSignUpRequestDto memberKakaoSignUpRequestDto){

        if(memberRepository.existsByKakaoUid(memberKakaoSignUpRequestDto.getKakaoUid())){
            throw new IllegalArgumentException("이미 존재하는 kakao 아이디가 있습니다.");
        }
        Member member = memberKakaoSignUpRequestDto.toEntity();
        member.setProviderId("KAKAO");// 나중에 Enum으로 리팩토링 가능할듯
        member.setNickname("외계인" + member.getKakaoUid());

        return memberRepository.save(member);
    }

    //로그인
    @Transactional
    public MemberLoginResponseDto signInMember(MemberLoginRequestDto memberloginRequestDto) {
        Optional<Member> memberOptional = memberRepository.findByEmail(memberloginRequestDto.getEmail());
        if (memberOptional.isEmpty()) {
            throw new IllegalArgumentException("올바르지 않은 Email입니다.");
        }
        Member member = memberOptional.get();
        String encodePassword = PasswordEncoder.sha256(memberloginRequestDto.getPassword());
        if (!encodePassword.equals(member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        member.setFirebaseUid(memberloginRequestDto.getFirebaseUid()); //로그인시 firebaseuid update

        MemberLoginResponseDto memberLoginResponseDto = MemberLoginResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .autoDiaryTime(member.getAutoDiaryTime())
                .diaryBaseName(member.getDiaryBaseName())
                .firebaseUid(member.getFirebaseUid())
                .providerId(member.getProviderId())
                .build();

        return memberLoginResponseDto;
    }

    //카카오 로그인
    @Transactional
    public MemberLoginResponseDto signInKakaoMember(MemberKakaoLoginRequestDto memberKakaoLoginRequestDto){
        Optional<Member> memberOptional = memberRepository.findByKakaoUid(memberKakaoLoginRequestDto.getKakaoUid());
        if(memberOptional.isEmpty()){
            throw new IllegalArgumentException("올바르지 않은 kakaoUid입니다");
        }
        Member member = memberOptional.get();

        MemberLoginResponseDto memberLoginResponseDto = MemberLoginResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .autoDiaryTime(member.getAutoDiaryTime())
                .diaryBaseName(member.getDiaryBaseName())
                .firebaseUid(member.getFirebaseUid())
                .kakaoUid(member.getKakaoUid())
                .providerId(member.getProviderId())
                .build();

        return memberLoginResponseDto;

    }

    //유저 조회
    @Transactional(readOnly = true)
    public MemberResponseDto findMember(Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 member입니다."));

        return MemberResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .autoDiaryTime(member.getAutoDiaryTime())
                .diaryBaseName(member.getDiaryBaseName())
                .firebaseUid(member.getFirebaseUid())
                .providerId(member.getProviderId())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }



    // 수정
    @Transactional
    public void modifyMember(Long memberId, MemberUpdateRequestDto memberUpdateRequestDto) {

        // 닉네임 중복 체크
        if (memberUpdateRequestDto.getNickname() != null) {
            Optional<Member> memberOptional = memberRepository.findByNickname(memberUpdateRequestDto.getNickname());
            if (memberOptional.isPresent() && !memberOptional.get().getMemberId().equals(memberId)) {
                throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
            }
        }

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 회원 정보 수정
        if (memberUpdateRequestDto.getNickname() != null) {
            member.setNickname(memberUpdateRequestDto.getNickname());
        }
        member.setAutoDiaryTime(memberUpdateRequestDto.getAutoDiaryTime());
        member.setDiaryBaseName(memberUpdateRequestDto.getDiaryBaseName());
    }

    //삭제
    @Transactional
    public void removeMember(long memberId) {
        memberRepository.deleteById(memberId);
    }

    /**
     * 날짜가 변경될 때 모든 회원들의 랜덤 요청 상태를 초기화한다.
     */
    @Transactional
    public void resetRandomExchange() {
        List<Member> allMemberList = memberRepository.findAll();

        for(Member member : allMemberList)
            member.setRandomExchanged(false);
    }

    @Transactional(readOnly = true)
    public boolean checkRandomChangeable(Long memberId) throws Exception {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new Exception("member does not exists."));
        return member.isRandomExchanged();
    }

    @Transactional
    public void setAllMembersRandomExchangedUnused() {
        List<Member> allMembers = memberRepository.findAll();
        for(Member member : allMembers)
            member.setRandomExchanged(false);
    }
}
