package com.bx.philosopher.model.dao;

import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.FileUtils;
import com.bx.philosopher.utils.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 存储关于书籍内容的信息(CollBook(收藏书籍),BookChapter(书籍列表),ChapterInfo(书籍章节)
 */

public class BookDaoManager {


    /**
     * 存储章节
     *
     * @param folderName bookid
     * @param fileName   章节名
     * @param content    章节内容
     */
    public static void saveChapterInfo(String folderName, String fileName, String content) {
        File file = getBookFile(folderName, fileName);
        //获取流并存储
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.close(writer);
        }
    }

    /**
     * 创建或获取存储文件
     *
     * @param folderName
     * @param fileName
     * @return
     */
    public static File getBookFile(String folderName, String fileName) {
        return FileUtils.getFile(Constant.BOOK_CACHE_PATH + folderName
                + File.separator + fileName + FileUtils.SUFFIX_PS);
    }


}
