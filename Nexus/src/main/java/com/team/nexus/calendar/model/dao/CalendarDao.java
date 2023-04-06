package com.team.nexus.calendar.model.dao;

import java.util.ArrayList;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.team.nexus.calendar.model.vo.Calendar;

@Repository
public class CalendarDao {

	public ArrayList<Calendar> selectCalendar(SqlSessionTemplate sqlSession,int userNo){
		
		return (ArrayList)sqlSession.selectList("calendarMapper.selectCalendar", userNo);
	}
}
