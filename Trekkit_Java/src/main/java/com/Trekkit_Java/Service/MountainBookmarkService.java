package com.Trekkit_Java.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Trekkit_Java.DAO.MountainBookmarkDAO;
import com.Trekkit_Java.Model.Mountain;

@Service
public class MountainBookmarkService {
	@Autowired
    private MountainBookmarkDAO mtbookmarkDAO;

    public void addBookmark(String userid, int mountainId) {
        mtbookmarkDAO.insertBookmark(userid, mountainId);
    }

    public void deleteBookmark(String userid, int mountainId) {
        mtbookmarkDAO.deleteBookmark(userid, mountainId);
    }

    public boolean isBookmarked(String userid, int mountainId) {
        return mtbookmarkDAO.existsBookmark(userid, mountainId) > 0;
    }

    public List<Mountain> getBookmarks(String userid) {
        return mtbookmarkDAO.selectBookmarksByUser(userid);
    }
}
