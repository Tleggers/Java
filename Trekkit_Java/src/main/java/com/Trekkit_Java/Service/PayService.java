package com.Trekkit_Java.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Trekkit_Java.DAO.PayDAO;

@Service
public class PayService {
	
	@Autowired private PayDAO pd;

	@Transactional
	public int addPoint(Long userid, int point) {

		// DB에 데이터를 넣고 성공하면 1을 리턴 아니면 0을 리턴
		int result = pd.updateUserPoint(userid, point);
        return result; // 1이면 성공, 0이면 실패
        
	}

	public int usePoint(Long userid, int point) {
		
	    // return payDao.usePoint(userid, point); // 실제 차감 처리
	    // 포인트 부족한지 확인하거나 예외처리는 여기서도 가능
	    return pd.usePoint(userid, point);
	}


}
