package io.github.hamsteak.trendlapse.collector.domain.v1;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BatchVideoCollectorTest {

//    @Test
//    void collect(
//            @Mock YoutubeDataApiCaller youtubeDataApiCaller,
//            @Mock YoutubeDataApiProperties youtubeDataApiProperties,
//            @Mock VideoFinder videoFinder,
//            @Mock VideoCreator videoCreator,
//            @Mock BatchChannelCollector batchChannelCollector,
//            @Mock ChannelReader channelReader
//    ) {
//        // given
//        List<String> videoYoutubeIds = List.of("video-youtube-id", "video-youtube-id");
//
//        when(videoFinder.findMissingVideoYoutubeIds(videoYoutubeIds))
//                .thenReturn(videoYoutubeIds);
//
//        when(youtubeDataApiProperties.getMaxResultCount())
//                .thenReturn(10);
//
//        when(youtubeDataApiCaller.fetchVideos(videoYoutubeIds))
//                .thenReturn(new VideoListResponse(List.of(
//                                new VideoResponse(1,
//                                        new VideoResponse.Snippet("title", 1, 1))
//                        ))
//                );
//
//        BatchVideoCollector batchVideoCollector = new BatchVideoCollector(
//                youtubeDataApiCaller, youtubeDataApiProperties, videoFinder, videoCreator, batchChannelCollector, channelReader);
//
//        // when
//        batchVideoCollector.collect(videoYoutubeIds);
//
//        // then
//    }
}