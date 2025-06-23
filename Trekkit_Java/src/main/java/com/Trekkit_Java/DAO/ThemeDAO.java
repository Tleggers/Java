package com.Trekkit_Java.DAO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.Trekkit_Java.DTO.OnlyTheme;
import com.Trekkit_Java.DTO.Theme;

@Mapper
public interface ThemeDAO {
   
	@Select("""
	        SELECT 
	            t.id AS theme_id,
	            t.name AS theme_name,
	            t.subtitle,
	            t.description AS theme_description,
	            t.image_url AS theme_image,

	            -- 산1
	            m1.id AS mountain1_id,
	            m1.name AS mountain1_name,
	            m1.image_url AS mountain1_image,
	            m1.region AS mountain1_region,
	            m1.difficulty AS mountain1_difficulty,
	         m1.duration_minutes AS mountain1_duration,
	         m1.height_m AS mountain1_height,
	            c1.name AS course1_name,
	            c1.description AS course1_description,
	            c1.route AS course1_route,
	            c1.note AS course1_note,

	            -- 산2
	            m2.id AS mountain2_id,
	            m2.name AS mountain2_name,
	            m2.image_url AS mountain2_image,
	            m2.region AS mountain2_region,
	            m2.difficulty AS mountain2_difficulty,
	         m2.duration_minutes AS mountain2_duration,
	         m2.height_m AS mountain2_height,
	            c2.name AS course2_name,
	            c2.description AS course2_description,
	            c2.route AS course2_route,
	            c2.note AS course2_note,

	            -- 산3
	            m3.id AS mountain3_id,
	            m3.name AS mountain3_name,
	            m3.image_url AS mountain3_image,
	            m3.region AS mountain3_region,
	            m3.difficulty AS mountain3_difficulty,
	         m3.duration_minutes AS mountain3_duration,
	         m3.height_m AS mountain3_height,
	            c3.name AS course3_name,
	            c3.description AS course3_description,
	            c3.route AS course3_route,
	            c3.note AS course3_note

	        FROM theme t
	        LEFT JOIN mountain_theme m1 ON t.mountain1 = m1.id
	        LEFT JOIN course c1 ON m1.course_id = c1.id
	        LEFT JOIN mountain_theme m2 ON t.mountain2 = m2.id
	        LEFT JOIN course c2 ON m2.course_id = c2.id
	        LEFT JOIN mountain_theme m3 ON t.mountain3 = m3.id
	        LEFT JOIN course c3 ON m3.course_id = c3.id
	        WHERE t.name = #{themeName}
	    """)
	Theme getThemeDetailByName(@Param("themeName") String themeName);
   
   @Select("""
          SELECT 
              id,
              name,
              subtitle,
              description,
              image_url,
              time,
              difficulty,
              numberofcourse
          FROM theme
      """)
      List<OnlyTheme> getThemes(); 

}
