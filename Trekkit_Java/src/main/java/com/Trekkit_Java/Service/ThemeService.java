package com.Trekkit_Java.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Trekkit_Java.DAO.ThemeDAO;
import com.Trekkit_Java.DTO.Theme;

@Service
public class ThemeService {
	
	@Autowired
    private ThemeDAO themeDAO;

	@Transactional(readOnly = true)
    public Theme getThemeDetail(String name) {
        return themeDAO.getThemeDetailByName(name);
    }

}
