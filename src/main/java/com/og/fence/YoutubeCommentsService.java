package com.og.fence;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.*;
import com.google.common.collect.Lists;
import org.springframework.core.io.FileSystemResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blurryface on 4/28/17.
 */
public class YoutubeCommentsService {
    private List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");
    private YouTube youtube;

    public YoutubeCommentsService() {
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest httpRequest) throws IOException {

            }
        }).setYouTubeRequestInitializer(new YouTubeRequestInitializer("AIzaSyCKiNY6Eq_DkuP1fuoZEpV3po3HOCyH9cc"))
                .setApplicationName("fence").build();
    }

    public FileSystemResource getAllComments(String videoId) {
        CommentThreadListResponse videoThreadListResponse = null;
        List<CommentSnippet> commentSnippets = new ArrayList<CommentSnippet>();

        try {
            videoThreadListResponse = youtube.commentThreads().list("id, snippet")
                    .setVideoId(videoId)
                    .setTextFormat("plainText")
                    .setMaxResults(100L).execute();
        } catch (IOException io) {
            io.printStackTrace();
        }


        while(true) {

            if (videoThreadListResponse != null && videoThreadListResponse.getItems() != null
                    && !videoThreadListResponse.getItems().isEmpty()) {

                List<CommentThread> videoComments = videoThreadListResponse.getItems();

                for (CommentThread videoComment : videoComments) {
                    CommentThreadSnippet threadSnippet = videoComment.getSnippet();
                    commentSnippets.add(threadSnippet.getTopLevelComment().getSnippet());
                    CommentListResponse listResponse = new CommentListResponse();

                    if (threadSnippet.getTotalReplyCount() > 0) {

                        try {
                            listResponse = youtube.comments().list("id, snippet")
                                    .setParentId(threadSnippet.getTopLevelComment().getId())
                                    .setMaxResults(100L)
                                    .setTextFormat("plainText").execute();
                        } catch (IOException io) {
                            io.printStackTrace();
                        }

                        if (listResponse != null && listResponse.getItems() != null){
                            List<Comment> comments = listResponse.getItems();
                            for (Comment comment : comments) {
                                commentSnippets.add(comment.getSnippet());
                            }
                        }
                    }
                }

                if (videoThreadListResponse.getNextPageToken() == null ||
                        videoThreadListResponse.getNextPageToken().isEmpty()){
                    break;
                } else{
                    try {
                        videoThreadListResponse = youtube.commentThreads().list("id, snippet")
                                .setTextFormat("plainText")
                                .setPageToken(videoThreadListResponse.getNextPageToken())
                                .setVideoId(videoId)
                                .setMaxResults(100L).execute();
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }

            } else {
                 break;
            }

        }

        //String fileLocation = "/Users/Blurryface/Documents/comments_"+ LocalDateTime.now() + ".csv";
        String fileName = "comments_"+ LocalDateTime.now() + ".csv";
        InputStream inputStream = null;
        try{
            CSVUtils.writeToCSVFile(commentSnippets, fileName);
        } catch (IOException io){
            io.printStackTrace();
        }

        return new FileSystemResource(fileName);
    }

    /*private void addComments(String id, List<CommentSnippet> commentSnippets){
        CommentListResponse listResponse = new CommentListResponse();

        try {
            listResponse = youtube.comments().list("id, snippet")
                    .setParentId(id)
                    .setMaxResults(100L)
                    .setTextFormat("plainText").execute();
        } catch (IOException io) {
            io.printStackTrace();
        }

        if (listResponse != null){
            List<Comment> comments = listResponse.getItems();
            for (Comment comment : comments) {
                addComments(comment.getId(), commentSnippets);
                commentSnippets.add(comment.getSnippet());
            }

        }


    }*/
}
