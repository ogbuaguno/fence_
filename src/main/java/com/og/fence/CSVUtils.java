package com.og.fence;

import com.google.api.services.youtube.model.CommentSnippet;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Blurryface on 4/28/17.
 */
public class CSVUtils {
    //private static String fileLocation = "/Users/Blurryface/Documents/comments_" + LocalDateTime.now() + ".csv";

    public static void writeToCSVFile(List<CommentSnippet> commentSnippets, String fileLocation) throws IOException{
        FileWriter fileWriter = new FileWriter(fileLocation);
        writeLineToCSV(commentSnippets, fileWriter);
    }

    private static void writeLineToCSV(List<CommentSnippet> commentSnippets, FileWriter writer) throws IOException{
        StringBuilder builder = new StringBuilder();

        builder.append("Username,").append("Date,").append("Star Rating,").append("Comment/Review,")
                .append("Link").append("\n");

        for (CommentSnippet commentSnippet : commentSnippets){
            builder.append(commentSnippet.getAuthorDisplayName()).append(",");
            builder.append(commentSnippet.getUpdatedAt() != null ? commentSnippet.getUpdatedAt() : commentSnippet.getPublishedAt()).append(",");
            builder.append(commentSnippet.getLikeCount()).append(",");
            builder.append(commentSnippet.getTextOriginal() != null ? removeCommas(commentSnippet.getTextOriginal()):
                    removeCommas(commentSnippet.getTextDisplay())).append(",");
            builder.append(commentSnippet.getAuthorChannelUrl() != null ? commentSnippet.getAuthorChannelUrl(): commentSnippet.getAuthorProfileImageUrl());

            builder.append("\n");
        }

        writer.append(builder.toString());
        writer.close();
    }

    private static String removeCommas(String comment){
        return comment.replaceAll("\n", "").replaceAll(",", "");
    }

}
