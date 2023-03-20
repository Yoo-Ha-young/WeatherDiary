package zerobase.weather.service;

import org.slf4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true) // 읽기만 가능함
public class DiaryService {
    // 1. 날씨 일기 저장 API : getWeatherString() 메소드에서 OpenWeatherMap API에서 날씨 데이터를 받아옵니다.
    @Value("${openweathermap.key}") // openweathermap.key 라는 변수를
    private String apiKey; // apiKey에 넣어준다.
    // 다이어리 컨트롤러에서 서비스를 불러오고
    // 다이어리 서비스에서 다이어리 레포지토리를 불러온다.
    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
    // 프로젝트 전체에 로거를 하나만 만들어서 WeatherApplication.class가 돌아갈 때 전체에 쓴다
    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    // 다이어리 서비스의 빈이 생성될 때 아래 생성자가 생성이 된다.
    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    // 매일 매달 매주 - 0초 0분 1시에 [날씨데이터를 저장하는 스케쥴링 API]
    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
//    @Scheduled(cron = "0/5 * * * * *")
    public void saveWeatherDate(){
        logger.info("Save Weather Data Success!");
        dateWeatherRepository.save(getWeatherFromAPi());  // getWeatherFromAPi로 데이터를 가져오고 파싱 해준 다음, DB쪽에 저장해주기
    }
    // 0/5 로  설정하면 0초부터 시작해서 5초 간격으로 매분 매주 매시에 동작이 된다.

    // [다이어리 작성, 새로운 다이어리 생성 API]
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) { // 다이어리 레포지토리를 통해 디비에 들어가야 한다.
//        // 1. open weather map에서 날씨 데이터 가져오기
//        String weatherData = getWeatherString();
//        // 2. 받아온 날씨 데이터 파싱하기
//        Map<String, Object> parsedWeather = parseWeather(weatherData);
// →  매일 1시에 저장된 데이터로 이용해줄 것이기 때문에 해당 내용은 수정된다.
        logger.info("started to create diary"); // info 레벨의 로그
        // 1. 날씨 데이터 가져오기(DB(domain)에서 가져오기)
        DateWeather nowDateWeather = getDateWeather(date); // service의 메서드를 통해서 db에 저장되어있는 날씨 데이터를 날짜로 가져와서,
        // DateWeather dateWeather (도메인) 쪽으로 저장해주기
        // Client <-(Dto)-> Controller <-(Dto)-> Service <-(Dto)-> Repository <-(Entity)-> DB

        // 2. 파싱된 데이터 + 일기 값 우리 db에 넣기
        Diary nowDiary = new Diary(); // 비어있는 다이어리 객체

        // 이미 아래에 파싱까지 해주는 기능의 저장 메서드가 있기 때문에 해당 코드는 사용 X
//        nowDiary.setWeather(parsedWeather.get("main").toString()); // jsonobject로 가져옴
//        nowDiary.setIcon(parsedWeather.get("icon").toString()); // jsonobject로 가져옴
//        nowDiary.setTemperature((Double) parsedWeather.get("temp")); // jsonobject로 가져옴
        //public void setDateWeather(DateWeather dateWeather) -- DateWeather Entity에 각 컬럼에 해당하는 데이터를 저장해주는 set 메서드
        nowDiary.setText(text); // 입력받는 일기
        nowDiary.setDate(date); // 입력받는 날짜
        nowDiary.setDateWeather(nowDateWeather); // 날짜로 가져온 날씨 정보 - 이 데이터가 많이 쌓이면 과거의 날씨 데이터까지(유료) 확인해볼 수 있는 장점이 있다.

        // Repository에 데이터를 저장해준다.
        diaryRepository.save(nowDiary);
        logger.info("end to create diary");
//        logger.error()
//        logger.warn()
    }

    // DateWeather 데이터에서 원하는 날짜의 날씨를 가져오는 함수
    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if(dateWeatherListFromDB.size() == 0){
            // 새로 api에서 날씨 정보를 가져와야한다
            // 과거날씨로 작성한다 하면,
            // 정책상 과거의 날씨는 가져올 수 없어서 현재 날씨를 가져오도록 한다.
            return getWeatherFromAPi();
        } else {
            return dateWeatherListFromDB.get(0); // findAllByDate(date)로 찾은 날씨의 첫번째 값을 가져온다.
        }
    }

    // 날씨 데이터를 불러와서 저장해주는 API : 데이터 가져오기/ 파싱하기, 매일 한번 작업
    private DateWeather getWeatherFromAPi(){ // DateWeather 엔터티
        // 1. open weather map에서 날씨 데이터 가져오기
        String weatherData = getWeatherString();
        // 2. 받아온 날씨 데이터 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);
        // 3. 파싱된 데이터 + 일기 값 우리 db에 넣기
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now()); // 현재 날씨를 가져온거라, 현재 날씨를 시점으로 가져올 수 있도록 로컬데이트로 써줌

        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parsedWeather.get("temp"));

        return dateWeather;
    }

    // [다이어리 조회 API : JPA로 직접 DB와 연결 되어있는 diaryRepository를 통해서 데이터를 가져와서 기능을 함]
    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        //logger.debug("read diary"); // debug 레벨의 로그
//        if(date.isAfter(LocalDate.ofYearDay(3050,1))){
//            throw new InvalidDate();
//        }
        return diaryRepository.findAllByDate(date);
                // 서비스 입장에서 db를 조회하려면 레포지토리를 통해야함
    }
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    // [다이어리 수정 : 리턴값은 없고 수정만, 그날짜의 가장 첫번째 일기만 수정하는 API]
    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text); // set으로 텍스트를 교체해줌
        diaryRepository.save(nowDiary);
    }
    // [원하는 날짜에 해당하는 다이어리 모두 삭제]
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    // [1. open weather map에서 날씨 데이터 가져오기]
    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;
        try {
            // 1. URL 객체를 생성하고, API를 호출하는 HttpURLConnection 객체를 생성합니다.
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 2. API 요청 방식을 설정하는 setRequestMethod() 메소드로 GET 방식을 설정합니다.
            connection.setRequestMethod("GET"); // 9.요청 메소드 설정
            int responseConde = connection.getResponseCode(); // 10. HTTP 응답 코드를 받아온다.

            /*
             * BufferedReader는 입력 스트림으로부터 문자열을 읽어오는 역할을 합니다.
             * 파일, 네트워크 등 다양한 소스로부터 데이터를 읽어올 수 있다.
             * BufferedReader는 버퍼링을 사용하여 입력 소스에서 데이터를 읽어들이고,
             * 읽어들인 데이터를 임시 저장소에 저장해두었다가, 요청이 있을 때마다
             * 임시 저장소에서 데이터를 반환합니다. 이렇게 함으로써, 읽어들이는 속도를 향상시킨다.
             * */
            BufferedReader br;

            /*
             * InputStreamReader는 바이트 스트림을 문자 스트림으로 변환하는 역할을 한다.
             * 바이트 스트림은 1byte씩 데이터를 읽고, 문자 스트림은 2byte씩 데이터를 읽어인다.
             * InputStreamReader는 이러한 바이트 스트림을 문자 스트림으로 변환하여
             * 문자열을 읽어들이는 것이 가능합니다.
             */
            if (responseConde == 200) { // 11. HTTP 응답 코드가 200(성공)인 경우 BufferedReader를 생성하여 InputStream으로부터 데이터를 읽어온다.
                br = new BufferedReader((new InputStreamReader((connection.getInputStream()))));
            } else { // 12. HTTP 응답 코드가 200이 아닌 경우 BufferedReader를 생성하여 에러 스트림으로부터 데이터를 읽어온다.
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;

            /*
             * StringBuilder는 문자열을 더해나가는 동적인 문자열 객체이다.
             * 문자열을 변경하거나 추가하는 작업이 빈번할 때 사용한다.
             * 문자열을 더하는 작업은 기존 문자열의 끝에 추가하기 때문에,
             * 추가되는 문자열의 길이가 불규칙적일 때도 효율적으로 작동할 수 있다.
             * 또한, String 객체와는 달리, 문자열이 변경되더라도 새로운 객체를 생성하지
             * 않기 때문에 메모리 사용량도 적게 든다. 이러한 이유로, 문자열 조작이 빈번한
             * 작업에서는 StringBuilder를 사용하는 것이 좋습다.
             */
            StringBuilder response = new StringBuilder(); // 4. StringBuilder 생성

            while ((inputLine = br.readLine()) != null) { // 5. BufferedReader를 사용하여 응답받은 데이터를 한 줄씩 읽어와 StringBuilder에 append
                response.append(inputLine);
            }
            br.close(); // 13. BufferedReader를 닫는다.

            // 6. StringBuilder를 문자열화하여 반환 :
            // StringBuilder 객체를 이용하여 응답 값을 문자열로 변환하여 반환한다.
            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }
    }

    // [ 2. 받아온 날씨(json) 데이터 파싱하기 ]
    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        // 파싱 작업이 정상적으로 작동하지 않을 때 예외 처리(괄호로 시작하지 않을 경우 등)
        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // 가져올 데이터 : weather.main, weather.icon, main.temp

        // 파싱 결과를 담을 Map 객체 resultMap를 생성합니다.
        Map<String, Object> resultMap = new HashMap<>();

        // 파싱한 JSON 데이터에서 "main" 속성의 값을 가져와
        // JSONObject 타입으로 변환한 후, mainData 변수에 할당합니다.
        JSONObject mainData = (JSONObject) jsonObject.get("main"); // jsonObject로 형변환을 꼭 해줘야함

        // 객체에서 "temp" 속성의 값을 가져옵니다.
        resultMap.put("temp", mainData.get("temp")); // mainData도 jsonObject객체로 get으로 가져올 수 있음

        // 파싱한 JSON 데이터에서 "weather" 속성의 값을 가져와 JSONObject 타입으로 변환한 후,
        // weatherData 변수에 할당합니다.

        // weather는 Array 형태로 [] 에 들어가 있기 때문에. Array로 먼저 파싱
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        // 파싱된 Array를 다시 JSONObject로 변환한다. 이때 데이터가 1개로 인덱스 0을 뽑아주면 된다.
        JSONObject weatherData = (JSONObject) weatherArray.get(0);

        // weatherData 객체에서 "main" 속성의 값을 가져와서 "main" 속성으로 저장합니다.
        resultMap.put("main", weatherData.get("main")); // weather의 main을 가져옴

        // weatherData 객체에서 "icon" 속성의 값을 가져와서 "icon" 속성으로 저장합니다.
        resultMap.put("icon", weatherData.get("icon")); // weather의 icon을 가져옴

        // 최종적으로 resultMap 객체를 반환합니다. 이 객체에는 "temp", "main", "icon" 속성이 포함됩니다.
        return resultMap;
    }
}

