package com.abhinav.cc_backend_layer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.abhinav.cc_backend_layer.model.CCMaster;
import com.abhinav.cc_backend_layer.model.CCMasterKey;
import com.abhinav.cc_backend_layer.model.AmountPerMonth;

@Repository
public interface CCMasterRepository extends JpaRepository<CCMaster, CCMasterKey> {

	List<CCMaster> findAllByKeyCode(String code);

	List<CCMaster> findAllByKeyStmtMonthYear(String monthYear);

	@Query(value = "select SUBSTR(STMTMONTHYEAR,0,2) as mm, SUBSTR(STMTMONTHYEAR,3,6) as yyyy, SUM(CAST(TOTALAMT as INTEGER)) as amount from  CC_MASTER_TEST group by SUBSTR(STMTMONTHYEAR,0,2),SUBSTR(STMTMONTHYEAR,2,4)", nativeQuery = true)
	List<AmountPerMonth> getAmountPerMonth();

	@Query(value = "select code, SUM(CAST(TOTALAMT as INTEGER)) as amount from  CC_MASTER_TEST group by code;", nativeQuery = true)
	List<AmountPerMonth> getAmountPerCard();
}
