package com.a101.fakediary.papago.api;

import com.a101.fakediary.papago.service.PapagoTranslator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class PapagoApi {
    private final PapagoTranslator papagoTranslator;
    private static final Logger logger = LoggerFactory.getLogger(PapagoApi.class);

//    @Async 자동생성 에러내던 주범 으아아악
    public String translateKorToEng(String text) { //메소드 불러서 바꾸고 싶은 내용 text에 넣으면 한 -> 영 바꿔서 return
        String translatedText = papagoTranslator.translateKorToEng(text).block();
        Pattern pattern = Pattern.compile("\"translatedText\":\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(translatedText);
        if (matcher.find()) {
            String trans = matcher.group(1);
            logger.info("번역완료 : {} -> {}", text, trans);
            return trans;
        }
        return null;
    }

    public String translateEngToKor(String text) { //메소드 불러서 바꾸고 싶은 내용 text에 넣으면 한 -> 영 바꿔서 return
        String translatedText = papagoTranslator.translateEngToKor(text).block();
        Pattern pattern = Pattern.compile("\"translatedText\":\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(translatedText);
        if (matcher.find()) {
            String trans = matcher.group(1);
            logger.info("번역완료 : {} -> {}", text, trans);
            return trans;
        }
        return null;
    }
}
