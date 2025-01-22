package com.abhinav.cc_backend_layer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.abhinav.cc_backend_layer.model.AmountPerMonth;
import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.model.CCMasterKey;

@Repository
public interface CCMasterRepository extends JpaRepository<CCMaster, CCMasterKey> {

	List<CCMaster> findAllByKeyCode(String code);

	List<CCMaster> findAllByKeyStmtMonthYear(String monthYear);

	@Query(value = "select CAST(SUBSTR(STMTMONTHYEAR,0,2) as INTEGER) as mm, SUBSTR(STMTMONTHYEAR,3,6) as yyyy, SUM(CAST(TOTALAMT as INTEGER)) as amount from  CC_MASTER_TEST where SUBSTR(STMTMONTHYEAR,3,6) = ? group by mm,yyyy order by mm", nativeQuery = true)
	List<AmountPerMonth> getAmountPerMonth(String year);

	@Query(value = "select c2.name, SUM(CAST(c1.TOTALAMT as INTEGER)) as amount from  CC_MASTER_TEST c1, CC_MASTER_NAMES c2 where c1.code=c2.code and substring(c1.STMTMONTHYEAR,3,7)= ? group by c2.name", nativeQuery = true)
	List<AmountPerMonth> getAmountPerCard(String year);
	
	List<CCMaster> findByCurrentStatusNot(String currentStatus);
}
