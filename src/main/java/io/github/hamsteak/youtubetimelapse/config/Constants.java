package io.github.hamsteak.youtubetimelapse.config;


public interface Constants {
    /** 데이터 수집 간격 */
    int COLLECT_INTERVAL = 1000 * 60 * 15;

    /** 한번에 조회가능한 최대 ( Video | Channel ) 개수 */
    int MAX_FETCH_COUNT = 50;
}
