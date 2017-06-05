package com.og.fence;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * Created by Blurryface on 4/28/17.
 */

@Controller
@RequestMapping("/comments")
public class YoutubeCommentsController {

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String get() {
        return "getcomments";
    }

    /*@RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public void listComments(@RequestParam("videoUrl") String videoUrl, HttpServletResponse response){
        String videoId = videoUrl.substring(videoUrl.indexOf("=") + 1);
        YoutubeCommentsService commentsService = new YoutubeCommentsService();
        InputStream inputStream = commentsService.getAllComments(videoId);
        try {
            FileCopyUtils.copy(inputStream, response.getOutputStream());
            inputStream.close();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=\"youtube_comments.csv\"");
            response.flushBuffer();
        } catch (IOException io){
            io.printStackTrace();
        }
    }*/

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public FileSystemResource listComments(@RequestParam("videoUrl") String videoUrl, HttpServletResponse response){
        String videoId = videoUrl.substring(videoUrl.indexOf("=") + 1);
        YoutubeCommentsService commentsService = new YoutubeCommentsService();
        FileSystemResource fsr = commentsService.getAllComments(videoId);
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=\"youtube_comments" + LocalDateTime.now() + ".csv\"");
            response.flushBuffer();
        } catch (IOException io){
            io.printStackTrace();
        }
        return fsr;
    }
}
